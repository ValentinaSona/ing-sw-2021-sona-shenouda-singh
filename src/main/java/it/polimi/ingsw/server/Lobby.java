package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Control flow the client when a connection with the server is established does the following steps:
 * try to register his nickname in order to join the LobbyRegisteredNicknamesMap calling
 * handleNicknameRegistration(nickname, connection)-->if everything is ok now the player can call the
 * handleLobbyJoiningRequest(nickname, connection)-->if everything is ok if the player is not the first one
 * he has to wait until all player join the lobby
 */

public class Lobby {
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());

    private final static int MIN_PLAYER_PER_GAME = 1;

    private final static int MAX_PLAYER_PER_GAME = 4;

    private static Lobby singleton;
    private volatile boolean active;

    //connection that have been registered with a valid nickname
    private final Map<Connection, String> registeredNicknamesMap;

    //connection that are in the registeredNicknamesMap but in order of arrive
    private final LinkedList<Connection> lobbyRequestingConnections;

    private final LinkedBlockingDeque<Connection> disconnectedPlayers;
    //the first connection to arrive that has the control on the player count
    private Connection firstConnection;


    private final Server server;

    private Match match;

    //maximum number of player
    private int currentLobbyPlayerCount;

    private final Object playerCountLock;


    public static Lobby getInstance(Server server) {
        if (singleton == null) {
            singleton = new Lobby(server);
            LOGGER.log(Level.INFO, "First creation of the singleton class lobby");
        }
        return singleton;
    }

    private Lobby(Server server) {
        this.server = server;
        this.lobbyRequestingConnections = new LinkedList<>();
        this.disconnectedPlayers = new LinkedBlockingDeque<>();
        this.registeredNicknamesMap = new ConcurrentHashMap<>();
        this.playerCountLock = new Object();
        this.active = true;
    }

    public boolean handleNicknameRegistration(String nickname, Connection connection) {
        //while a player is trying to register,  the map can't be modified by other threads
        synchronized (registeredNicknamesMap){
            if (registeredNicknamesMap.containsValue(nickname) || registeredNicknamesMap.containsKey(connection)) {
                LOGGER.log(Level.INFO, " we have already a player with that name or a connection from that client");
                return false;
            } else {
                LOGGER.log(Level.INFO,nickname+" connected to the lobby");
                registeredNicknamesMap.put(connection, nickname);
                return true;
            }
        }
    }

    public void removeNickname(String nickname) {
        synchronized (registeredNicknamesMap){
            registeredNicknamesMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(nickname))
                    .forEach(entry -> registeredNicknamesMap.remove(entry.getKey(), entry.getValue()));

        }
    }

    public boolean setLobbyMaxPlayerCount(int playerCount, Connection connection) {

        if (firstConnection == null || connection != firstConnection) {
            return false;
        }

        if (playerCount > MAX_PLAYER_PER_GAME || playerCount < MIN_PLAYER_PER_GAME) {
            return false;
        }

        synchronized (playerCountLock){
            if (currentLobbyPlayerCount != 0) {
                return false;
            }

            LOGGER.log(Level.INFO, registeredNicknamesMap.get(connection)
                    +"has set the playerCount to "+ playerCount);
            currentLobbyPlayerCount = playerCount;
            playerCountLock.notifyAll();
        }
        return true;
    }

    public int getCurrentLobbyPlayerCount() {
        return currentLobbyPlayerCount;
    }

    /**
     * This method handles a request to join the lobby by a player. If nickname and connection are both valid,
     * the method adds the user to the queue, notifies all waiting threads and sends an OK on the connection
     *
     * @param nickname   a String representing the nickname of the user is communicating
     * @param connection the Connection from which the user is
     * @return true if there were no errors
     */
    public boolean handleLobbyJoiningRequest(String nickname, Connection connection){
        //if the player has already registered itself in the Map he can proceed to be registered in
        //the lobbyRequestingConnection
        synchronized (registeredNicknamesMap){
            if(!registeredNicknamesMap.containsValue(nickname) || !registeredNicknamesMap.containsKey(connection)){
                LOGGER.log(Level.INFO,nickname+ "isn't registered and he is trying to join the lobby");
                return false;
            }
        }
        synchronized (lobbyRequestingConnections){
            lobbyRequestingConnections.add(connection);
            LOGGER.log(Level.INFO, nickname+" joined successfully the lobby");
            if(lobbyRequestingConnections.getFirst() != connection){
                //informing the client he is not the first one
                connection.send(StatusMessage.JOIN_LOBBY);
                //updates the client about the number of player in the lobby
                updateMessage();
            }
            lobbyRequestingConnections.notifyAll();
        }

        return true;
    }

    public boolean handleLobbyDisconnection(Connection connection){
        synchronized (lobbyRequestingConnections){
            //erase itself from records in the server, if it is not the firstConnection
            lobbyRequestingConnections.removeIf( c-> c.equals(connection));
            registeredNicknamesMap.remove(connection);
            server.removeHandler(connection);

            synchronized (playerCountLock){
                if(firstConnection != null && firstConnection.equals(connection)
                && currentLobbyPlayerCount == 0){
                    //we set to -1 only if the firstConnection was the
                    //connection in the lobby otherwise we continue to wait for the number of player that were
                    //decided by the firstConnection
                    currentLobbyPlayerCount = -1;
                    playerCountLock.notifyAll();
                }
            }
            connection.closeConnection();
        }

        return true;
    }

    public void start(){
        while(active){
            this.waitForFirstConnection();

            //after the first player is arrived he has to choose the numOfPlayer for the game
            this.waitForCurrentPlayerCount();

            Map<Connection, String> participants = this.getAllParticipants();

            //if the first player is still connected
            if(!participants.isEmpty()){

                this.match= new Match(server);
                List<Connection> connectionList = new ArrayList<>();
                for(Connection c : participants.keySet()){
                    connectionList.add(c);
                }
                //randomizing the order of the users
                Collections.shuffle(connectionList);
                for(Connection c : connectionList){
                    match.addParticipant(participants.get(c), c);
                }
                server.submitMatch(match);
                waitForDisconnectedPlayers();
                stop();
            }
            //if the first player disconnected while no one was in the lobby what will happen is
            //that we will do another time the while

        }

    }

    public boolean handleGameReconnection(String nickname, Connection connection){
        synchronized (disconnectedPlayers){
            try{
                disconnectedPlayers.put(connection);
                disconnectedPlayers.notifyAll();
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private void waitForDisconnectedPlayers(){
        while(isActive()){
            try{
                Connection connection = disconnectedPlayers.take();
                Game model = Game.getInstance();
                List<User> disconnected = model.getDisconnectedPlayers();
                ConnectionSetupHandler handler = server.getHandlerMap().get(connection);
                if(disconnected.contains(new User(handler.getNickname()))){
                    //devo gestire la riconnessione
                    server.removeHandlerForReconnection(connection);
                }else{
                    //gestisco disconnessione
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    private void waitForFirstConnection(){
        synchronized (lobbyRequestingConnections){
            //waiting for the first player to call handleLobbyJoiningRequest method to populate map and LinkedList
            while (lobbyRequestingConnections.isEmpty()){
                try{
                    lobbyRequestingConnections.wait();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
            firstConnection = lobbyRequestingConnections.getFirst();
        }
    }

    private void waitForCurrentPlayerCount(){
        synchronized (playerCountLock){
            currentLobbyPlayerCount = 0;
            //the operation is gone well but we need other info
            firstConnection.send(StatusMessage.SET_COUNT);
            while(currentLobbyPlayerCount == 0){
                try {
                    //waiting for the first player to call setLobbyMaxPlayerCount
                    playerCountLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void updateMessage(){
        ArrayList<User> lobbyUsers = new ArrayList<>();
        for(Connection con: registeredNicknamesMap.keySet()){
            if(lobbyRequestingConnections.contains(con))
                lobbyUsers.add(new User(registeredNicknamesMap.get(con)));
        }
        for(Connection con : lobbyRequestingConnections){
            con.send(new ServerUpdateLobbyMessage(lobbyUsers, currentLobbyPlayerCount ));
        }
    }

    private Map<Connection, String> getAllParticipants(){
        Map<Connection, String> participants = new ConcurrentHashMap<>();

        synchronized (lobbyRequestingConnections){
            //when this method return i have all the requested connection
            this.waitForParticipants();
            //check if in the mean time the firstConnection has disconnected
            boolean firstPlayerDisconnected =/*the first player disconnected while no one was in the lobby --> playerCount == -1 */
                    currentLobbyPlayerCount == -1 || !firstConnection.equals(lobbyRequestingConnections.get(0));

            if(!firstPlayerDisconnected){
                for(Connection connection : lobbyRequestingConnections){
                    participants.put(connection, registeredNicknamesMap.get(connection));
                }

                for(int i=0; i<currentLobbyPlayerCount; i++){
                    lobbyRequestingConnections.removeFirst();
                }
            }else{
                //the first player disconnected we need to choose again the lobby count

                //erase itself from records in the server, if it is not the firstConnection
                for(Connection c: lobbyRequestingConnections){
                    lobbyRequestingConnections.remove(c);
                    registeredNicknamesMap.remove(c);
                    server.removeHandler(c);
                    c.closeConnection();
                }
            }
        }

        return participants;
    }

    /**
     * This method wait for connection until we reach the currentLobbyPlayerCount
     */
    private void waitForParticipants(){
        //thread can acquire a lock that it already owns.
        synchronized (lobbyRequestingConnections){
            //until i reach the total number of player or the firstPlayer disconnect
            while (lobbyRequestingConnections.size() < currentLobbyPlayerCount &&
                    lobbyRequestingConnections.get(0).equals(firstConnection)){
                try{
                    //waiting for someone to call handleLobbyJoining request
                    lobbyRequestingConnections.wait();
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * This method stops the main ServerLobbyBuilder thread
     */
    public void stop() {
        active = false;
        singleton = null;
    }

    public boolean isActive() {
        return active;
    }
}

