package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controll flow the client when a connection with the server is established does the following setps:
 * try to register his nickname in order to join the LobbyRegisteredNickanmesMap calling
 * handleNicknameRegistration(nickname, connection)-->if everything is ok now the player can call the
 * handleLobbyJoiningRequest(nickname, connection)-->if everything is ok if the player is not the first one
 * he has to wait until all player join the lobby
 */

public class Lobby {
    private final static int MIN_PLAYER_PER_GAME = 1;
    private final static int MAX_PLAYER_PER_GAME = 4;


    private static Lobby singleton;
    //server socket created by the server and passed to the lobby
    private final LinkedList<Connection> lobbyRequestingConnections;
    //connection that have been registered
    private final Map<Connection, String> registeredNicknamesMap;
    //the first connection to arrive that has the control on the player count
    private Connection firstConnection;
    //maximum number of player
    private final Server server;

    private int currentLobbyPlayerCount;

    private final Object playerCountLock;

    private volatile boolean active;

    public static Lobby getInstance(Server server) {
        if (singleton == null) {
            singleton = new Lobby(server);
        }
        return singleton;
    }

    private Lobby(Server server) {
        this.server = server;
        this.lobbyRequestingConnections = new LinkedList<>();
        this.registeredNicknamesMap = new ConcurrentHashMap<>();
        this.playerCountLock = new Object();
        this.active = true;
    }

    public boolean handleNicknameRegistration(String nickname, Connection connection) {
        //while a player is trying to register,  the map can't be modified by other threads
        synchronized (registeredNicknamesMap){
            if (registeredNicknamesMap.containsValue(nickname) || registeredNicknamesMap.containsKey(connection)) {
                //we have already a player with that name or a connection from that client
                return false;
            } else {
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
        synchronized (registeredNicknamesMap){
            if(!registeredNicknamesMap.containsValue(nickname) || !registeredNicknamesMap.containsKey(connection)){
                return false;
            }
        }
        synchronized (lobbyRequestingConnections){
            lobbyRequestingConnections.add(connection);
            lobbyRequestingConnections.notifyAll();
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
                Match match= new Match(server);
                for(Connection c : participants.keySet()){
                    match.addParticipant(participants.get(c), c);
                }
                //server.submit(match);
            }//TODO come comportarsi nel caso in cui si disconnetta il primo player
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
            firstConnection.send(StatusMessage.CONTINUE);
            while(currentLobbyPlayerCount == 0){
                try {
                    //waiting for the first player to call setPlayercount
                    this.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private Map<Connection, String> getAllParticipants(){
        Map<Connection, String> participants = new ConcurrentHashMap<>();

        synchronized (lobbyRequestingConnections){
            //when this method return i have all the requested connection
            this.waitForParticipants();
            //check if in the mean time the firstConnection has disconnected
            boolean firstPlayerDisconnected =
                    currentLobbyPlayerCount == -1 || !firstConnection.equals(lobbyRequestingConnections.get(0));

            if(!firstPlayerDisconnected){
                for(Connection connection : lobbyRequestingConnections){
                    participants.put(connection, registeredNicknamesMap.get(connection));
                }

                for(int i=0; i<currentLobbyPlayerCount; i++){
                    lobbyRequestingConnections.removeFirst();
                }
            }
        }

        return participants;
    }

    /**
     * This method wait for connection until we reach the currentLobbyPlayerCount
     */
    private void waitForParticipants(){
        //DOPPIO LOCK NNON MI TORNA
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

}

