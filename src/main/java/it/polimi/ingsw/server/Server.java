package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private int serverPort;
    private final ServerSocket serverSocket;
    //this map contains the connection and the corresponding handler that will interpret the setup messages of the client
    private final Map<Connection, ConnectionSetupHandler> handlerMap;
    private final ExecutorService executorService;
    private Match match;
    private Lobby lobby;



    public Server(int port) throws IOException {
        this.serverPort = port;
        this.serverSocket = new ServerSocket(serverPort);
        this.lobby = Lobby.getInstance(serverSocket);
        this.executorService = Executors.newCachedThreadPool();
        this.handlerMap = new ConcurrentHashMap<>();
        new Thread(()->lobby.start());
    }

    public void start() throws IOException {
        while (!serverSocket.isClosed()){
            try{
                Socket inboundSocket = serverSocket.accept();
                Connection currentConnection = new Connection(inboundSocket);;
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

        serverSocket.close();
    }

}
