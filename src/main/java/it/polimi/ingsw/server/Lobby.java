package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;
import it.polimi.ingsw.utils.persistence.SavedState;

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
    /**
     * Boolean that represent that maintains the threadLobby running
     */
    private volatile boolean active;
    /**
     * Boolean that is used to instruct the lobby if it has to create a new game or
     * it has to load it from a file
     */
    private volatile boolean loadGameFromFile;
    /**
     * Lock used ta access safely the content of loadGameFromFile variable
     */
    private final Object loadGameLock = new Object();
    /**
     * Map that contains a reference to the connection and the name of the players that have called
     * and terminated successfully the method handleNicknameRegistration
     */
    private final Map<Connection, String> registeredNicknamesMap;

    /**
     * List that contains a reference to the connection who have already been registered in the
     * registeredNicknamesMap and have called and terminated successfully the handleJoinLobby method
     */
    private final LinkedList<Connection> lobbyRequestingConnections;

    /**
     * List that contains the users that are trying to reconnect to a ongoing game
     */
    private final LinkedBlockingDeque<Connection> disconnectedPlayers;
    /**
     * First connection takes the value of the first user that after calling handleNicknameRegistration calls also the handleJoinLobby
     * method
     */
    private Connection firstConnection;
    /**
     * This list is used when the first player has choose to load the game from a file, and it
     * contains the nicknames of all the players that were in previous game
     */
    private List<User> userList;

    /**
     * A reference to the current server
     */
    private final Server server;

    /**
     * A reference to the current match tha is basically a class with a run method
     * that after doing the setup of the model, controller and view components, waits for new messages
     * from the remoteViews and makes the controller process them
     */
    private Match match;

    /**
     * Used to inform the lobby if a game that was started is now ended for some reason
     */
    private boolean activeMatch;
    /**
     * Lock used to access safely the content of activeMatch
     */
    private final Object activeMatchLock = new Object();

    /**
     * Used to control the flow of the lobby and it can been modified both internally and
     * externally
     */
    private LobbyState lobbyState;

    /**
     * Lock used to acess safely the content of lobbyState
     */
    private final Object stateLock;

    /**
     * Current number of player that has been chosen by the fist player
     */
    private int currentLobbyPlayerCount;

    /**
     * Lock to access safely the content of currentLobbyPlayerCount
     */
    private final Object playerCountLock;

    /**
     * Lock used when loading a game from a file
     */
    private final Object resumeGameObject = new Object();

    /**
     * Used by the server to instantiate a lobby
     * @param server on witch the lobby is running
     * @return the lobby object
     */
    public static Lobby getInstance(Server server) {
        if (singleton == null) {
            singleton = new Lobby(server);
            LOGGER.log(Level.INFO, "First creation of the singleton class lobby");
        }
        return singleton;
    }

    /**
     * Constructor tha set the initial value of the attributes and create all the needed objects
     * @param server
     */
    private Lobby(Server server) {
        this.server = server;
        this.lobbyRequestingConnections = new LinkedList<>();
        this.disconnectedPlayers = new LinkedBlockingDeque<>();
        this.registeredNicknamesMap = new ConcurrentHashMap<>();
        this.playerCountLock = new Object();
        this.stateLock = new Object();
        this.active = true;
        this.lobbyState = LobbyState.LOBBY_SETUP;
        this.loadGameFromFile = false;
    }


    private void setLoadGameFromFile(boolean load){
        synchronized (loadGameLock){
            this.loadGameFromFile = load;
        }
    }

    private boolean isLoadGameFromFile(){
        synchronized (loadGameLock){
            return loadGameFromFile;
        }
    }

    /**
     * Called instead of setLobbyMaxPlayerCount by the ClientLoadGameMessage when restoring from file.
     * Invokes the aforementioned method passing it the size of the saved game.
     * @param connection the player sending the message.
     * @return true if successful.
     */
    public boolean handleResumeGame(Connection connection){
        if (firstConnection == null || connection != firstConnection) {
            return false;
        }

        synchronized (resumeGameObject){
            LOGGER.log(Level.INFO, "Il seguente giocatore sta tentando di fare il load da file");
            SavedState.load();
            userList = new ArrayList<>();
            Game.getInstance().getPlayers().forEach(player -> userList.add(new User(player.getNickname())));
            LOGGER.log(Level.INFO, ""+userList.size());
            setLobbyMaxPlayerCount(userList.size(), firstConnection);

            setLoadGameFromFile(true);

            List<Connection> toRemove = new ArrayList<>();
            registeredNicknamesMap.forEach((key, value)-> {
                if(!userList.contains(new User(value)))
                    toRemove.add(key);
            });

            toRemove.forEach(con->handleLobbyDisconnection(con));

            if(getCurrentLobbyPlayerCount() > 0)
                updateMessage();
            //all the player that were connected to the lobby included the one who asked to resume the game
            //were not part of the previous game

            return true;
        }


    }

    /**
     * Called by the users that are trying to connect to the lobby.
     * Checks that this connection has not already registered with another name and that the chosen nicknames is
     * not already in use by another player
     * @param nickname chosen by the user
     * @param connection connection of the user
     * @return true if successful, otherwise false
     */
    public boolean handleNicknameRegistration(String nickname, Connection connection) {
        //while a player is trying to register,  the map can't be modified by other threads
        synchronized (registeredNicknamesMap){
            if (registeredNicknamesMap.containsValue(nickname) || registeredNicknamesMap.containsKey(connection)) {
                LOGGER.log(Level.INFO, " we have already a player with that name or a connection from that client");
                return false;
            } else {

                if(isLoadGameFromFile() && !userList.contains(new User(nickname))){
                    handleLobbyDisconnection(connection);
                    return false;
                }
                LOGGER.log(Level.INFO,nickname+" connected to the lobby");
                registeredNicknamesMap.put(connection, nickname);
                return true;
            }
        }
    }

    /**
     * Called by the ClientSetPlayersCountMessage sent by the first player (after they receive the SET_COUNT).
     * @param playerCount the number of players expected in the game.
     * @param connection the player's connection.
     * @return true if successful, false if not.
     */
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
                    +" has set the playerCount to "+ playerCount);
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
    public boolean handleLobbyJoiningRequest(ClientJoinLobbyMessage message, String nickname, Connection connection){
        //if the player has already registered itself in the Map he can proceed to be registered in
        //the lobbyRequestingConnection
        synchronized (registeredNicknamesMap){
            if(!registeredNicknamesMap.containsValue(nickname) || !registeredNicknamesMap.containsKey(connection)){
                LOGGER.log(Level.INFO,nickname+ " isn't registered and he is trying to join the lobby");
                return false;
            }
        }


        synchronized (stateLock){
            if(!lobbyState.equals(LobbyState.LOBBY_SETUP)){
                handleForceLobbyDisconnection(connection);
                return false;
            }

        }

        if(firstConnection != null && (isLoadGameFromFile() != message.isLoadFromGame())){
            // The lobby is waiting for players in  order to create a new game and not to load it
            handleLobbyDisconnection(connection);
        }

        synchronized (lobbyRequestingConnections){
            lobbyRequestingConnections.add(connection);
            LOGGER.log(Level.INFO, nickname+" joined successfully the lobby");
            if(lobbyRequestingConnections.getFirst() != connection){
                //informing the client he is not the first one
                connection.send(StatusMessage.JOIN_LOBBY);
                //updates the client about the number of player in the lobby
                if(getCurrentLobbyPlayerCount() > 0)
                    updateMessage();
            }
            lobbyRequestingConnections.notifyAll();
        }

        return true;
    }

    /**
     * Ths methods handle an explicit disconnection of a player while in the lobby and is called when
     * the client send a DisconnectionMessage
     * @param connection requesting disconnection
     * @return true if successful
     */
    public boolean handleLobbyDisconnection(Connection connection){
        synchronized (lobbyRequestingConnections){
            //erase itself from records in the server, if it is not the firstConnection
            lobbyRequestingConnections.remove(connection);
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

    /**
     * Method called when the lobby is launched on a new thread
     */
    public void start(){
        while(isActive()){
            firstConnection = null;
            lobbyRequestingConnections.clear();
            registeredNicknamesMap.clear();
            currentLobbyPlayerCount = 0;

            LOGGER.log(Level.INFO,"waiting for first connection");
            this.waitForFirstConnection();
            // After the first player has arrived they has to choose the numOfPlayer for the game
            this.waitForCurrentPlayerCount();

            Map<Connection, String> participants = this.getAllParticipants();

            //if the first player is still connected
            if(!participants.isEmpty()){
                lobbyState = LobbyState.GAME_SETUP;

                this.match= new Match(server, this);
                List<Connection> connectionList = new ArrayList<>(participants.keySet());

                //randomizing the order of the users
                Collections.shuffle(connectionList);
                for(Connection c : connectionList){
                    match.addParticipant(participants.get(c), c);
                }

                if(isLoadGameFromFile()){
                    match.setLoadFromFile(true);
                }
                server.submitMatch(match);


                synchronized (stateLock){
                    while(getLobbyState().equals(LobbyState.GAME_SETUP)){
                        try{
                            stateLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }


                if(isActiveMatch()){
                    waitForDisconnectedPlayers();
                }
            }
            //we have to clean the lobby

        }

    }

    /**
     * This method handles the disconnection of a player when is the lobby or match requesting it
     * @param connection to disconnect
     * @return true if successful
     */
    public boolean handleForceLobbyDisconnection(Connection connection){
        synchronized (lobbyRequestingConnections) {
            registeredNicknamesMap.remove(connection);
            lobbyRequestingConnections.remove(connection);
            server.removeHandler(connection);
            connection.closeConnection();
        }
        return true;
    }

    /**
     * This method handle the request of reconnection to an ongoing game
     * @param nickname of the player trying to reconnect
     * @param connection of the player tring to reconnect
     * @return true if successful
     */
    public boolean handleGameReconnection(String nickname, Connection connection){

        boolean status = handleNicknameRegistration(nickname, connection);

        synchronized (disconnectedPlayers){
            try{
                if(status && lobbyState.equals(LobbyState.IN_GAME)){
                    disconnectedPlayers.put(connection);
                    disconnectedPlayers.notifyAll();
                    return true;
                }else{
                    //qualcuno sta provando a riconnettersi con lo stesso nickname oppure in una fase in cui non c'è una partita
                    return handleForceLobbyDisconnection(connection);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * This method is called by the lobby after a game is started/loaded successfully and so no
     * setup operation are needed
     */
    private void waitForDisconnectedPlayers(){
        while(isActiveMatch()){
            try{
                Connection connection = disconnectedPlayers.take();
                Game model = Game.getInstance();
                List<User> disconnected = model.getDisconnectedPlayers();
                ConnectionSetupHandler handler = server.getHandlerMap().get(connection);
                if(disconnected.contains(new User(handler.getNickname()))){
                    //devo gestire la riconnessione
                    connection.send(StatusMessage.RECONNECTION_OK);


                    server.removeHandlerForReconnection(connection);
                    lobbyRequestingConnections.remove(connection);
                    registeredNicknamesMap.remove(connection);

                    match.handleReconnection(handler.getNickname(), connection);
                }else{
                    //gestisco disconnessione
                    handleForceLobbyDisconnection(connection);

                }
            } catch (InterruptedException e) {
                //the activeMatchBoolean has been modified
                Thread.currentThread().setName("Lobby");
            }
        }
    }

    /**
     * This method is called by the lobby and sets the tread on witch the lobby is running in wait
     * until the first connection is added to lobbyRequestingConnection
     */
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

    /**
     * Sends the SET_COUNT to the first player that connects, waits for the answer.
     * The client must invoke setLobbyMaxPlayerCount() by sending a ClientSetPlayersCountMessage message to continue.
     */
    private void waitForCurrentPlayerCount(){
        synchronized (playerCountLock){
            currentLobbyPlayerCount = 0;
            // The operation has gone well but we need other info
            firstConnection.send(StatusMessage.SET_COUNT);
            while(currentLobbyPlayerCount == 0){
                try {
                    // waiting for the first player to call setLobbyMaxPlayerCount
                    playerCountLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, e.getMessage());
                }
            }
        }
    }

    /**
     * Used by the lobby to inform the client about the current number of player inside the lobby
     */
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

    /**
     * Used after the required number of player has joined the lobby and handles the removal of the ConnectionSetupHandler
     * observer from the connection
     * @return
     */
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
                    registeredNicknamesMap.remove(connection);
                }

                for(int i=0; i<currentLobbyPlayerCount; i++){
                    lobbyRequestingConnections.removeFirst();
                }

                //disconnecting remaining users that joined the lobby but will not be part of the game
                for(int i= 0; i< lobbyRequestingConnections.size(); i++){
                    handleForceLobbyDisconnection(lobbyRequestingConnections.get(i));
                }
            }else{
                //the first player disconnected we need to choose again the lobby count

                //erase itself from records in the server, if it is not the firstConnection
                for(int i= 0; i< lobbyRequestingConnections.size(); i++){
                    handleForceLobbyDisconnection(lobbyRequestingConnections.get(i));
                }
            }
            firstConnection = null;
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

    public boolean isActiveMatch() {
        synchronized (activeMatchLock){
            return activeMatch;
        }
    }

    public void setActiveMatch(boolean activeMatch) {
        synchronized (activeMatchLock){
            this.activeMatch = activeMatch;
            activeMatchLock.notifyAll();
        }
    }

    public void setLobbyState(LobbyState lobbyState) {
        synchronized (stateLock){
            this.lobbyState = lobbyState;
            stateLock.notifyAll();
        }
    }

    public LobbyState getLobbyState() {
        synchronized (stateLock){
            return lobbyState;
        }
    }

}

