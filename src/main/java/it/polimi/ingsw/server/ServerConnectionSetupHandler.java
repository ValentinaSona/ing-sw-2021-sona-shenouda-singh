package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.observer.LambdaObserver;

/**
 * This class handles for every client the first steps of game setup
 * Handling the request of a nickname and the joining of a lobby
 */
public class ServerConnectionSetupHandler implements LambdaObserver {
    private final Server server;
    /**
     * ClientConnection of which the handler is observer for new messages
     * and to witch it needs to send messages
     */
    private final Connection connection;

    private String nickname;

    public ServerConnectionSetupHandler(Server server, Connection connection){
        this.server = server;
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }


    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }
}