package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    /**
     * Used to accept new incoming client connections
     */
    private final ServerSocket serverSocket;
    /**
     *     this map contains the connection and the corresponding handler
     *     that will interpret the setup messages of the client
     */
    private final Map<Connection, ConnectionSetupHandler> handlerMap;
    /**
     *     used to give the provide threads for the connections that manage the inbound sockets
     */
    private final ExecutorService executorService;

    /**
     * Thread used by the lobby
     */
    private final Thread lobbyThread;

    /**
     * Reference to the current lobby
     */
    private Lobby lobby;

    /**
     *
     * @param port the serverPort
     * @throws IOException thrown by the serverSocket if the creation of the object failed
     */
    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.lobby = Lobby.getInstance(this);
        this.executorService = Executors.newCachedThreadPool();
        this.handlerMap = new ConcurrentHashMap<>();
        lobbyThread = new Thread(()->lobby.start());
        lobbyThread.start();
    }

    /**
     * This is the main method of the server, which runs infinitely until the server is shut down.
     * This method accepts inbound connections and dispatches them
     *
     */
    public void start()  {
        while (!serverSocket.isClosed()){
            try{
                Socket inboundSocket = serverSocket.accept();
                Connection currentConnection = new Connection(inboundSocket);
                ConnectionSetupHandler setupHandler = new ConnectionSetupHandler(lobby, currentConnection);

                synchronized (handlerMap){
                    handlerMap.put(currentConnection, setupHandler);
                }

                currentConnection.addObserver(setupHandler, (observer, transmittable) ->
                        ( (ConnectionSetupHandler) observer).update(transmittable) );

                executorService.submit(currentConnection);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is called by the lobby when a game is ready to start
     * @param match the match that has been created by the lobby
     */
    public void submitMatch(Match match){

        Thread matchThread = new Thread(match);
        matchThread.start();
        Map<String, Connection> participants = match.getParticipantMap();
        for(Connection connection : participants.values()){
            synchronized (handlerMap){
                connection.removeObserver(handlerMap.get(connection));
                handlerMap.remove(connection);
            }
        }
    }

    /**
     * This method can be called by the server itself in the submitMatch() method
     * or by the lobby if it has to handle a disconnection.
     * @param connection the connection subjected to removal.
     */
    public void removeHandler(Connection connection){
        synchronized (handlerMap){
            handlerMap.remove(connection);
        }
    }

    /**
     * Used by the lobby when it has to handle the reconnection of a player to an ongoing match
     * @param connection Connection from whom the ConnectionSetupHandler has to be removed
     */
    public void removeHandlerForReconnection(Connection connection){
        synchronized (handlerMap){
            connection.removeObserver(handlerMap.get(connection));
            handlerMap.remove(connection);
        }
    }


    public void shutDown(){
        try{
            serverSocket.close();
        }catch (IOException e){
            //do nothing
            e.printStackTrace();
        }
    }

    public Map<Connection, ConnectionSetupHandler> getHandlerMap() {
        return handlerMap;
    }

    public Thread getLobbyThread(){
        return lobbyThread;
    }
}
