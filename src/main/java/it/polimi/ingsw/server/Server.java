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
    private final ExecutorService executorService;
    private Match match;
    private final Map<Connection, ServerConnectionSetupHandler> handlerMap;


    public Server(int port){
        this.serverPort = port;
        this.serverSocket = new ServerSocket(serverPort);
        this.executorService = Executors.newCachedThreadPool();
        this.handlerMap = new ConcurrentHashMap<>();

    }
    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();//this port is not available
        }

        System.out.println("Server ready");
        while (true){
            try{
                Socket clientConnection = serverSocket.accept();
                //adesso devo gestire questa connessione in entrata
                //ossia vado a verificare se è il primo altrimenti lo aggiungo alla coda
                //di gente per iniziare la partita
            }catch(IOException e){
                break; //In case the serverSocket gets closed
            }
        }

        serverSocket.close();
    }

}
