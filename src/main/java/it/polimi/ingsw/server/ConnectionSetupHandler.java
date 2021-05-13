package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObserver;

/**
 * This class handles for every client the first steps of game setup
 * Handling the request of a nickname and the joining of a lobby
 */
public class ConnectionSetupHandler implements LambdaObserver {
    /**
     * ClientConnection of which the handler is observer for new messages
     * and to witch it needs to send messages
     */
    private final Connection connection;

    //this value is set when the handler receive a ClientSetNicknameMessage
    private String nickname;

    private final Lobby lobby;

    public ConnectionSetupHandler(Lobby lobby, Connection connection){
        this.connection = connection;
        this.lobby = lobby;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Lobby getLobby() {
        return lobby;
    }

    /**
     * This method is called by the connection in the setup process
     * when the connection receive message from the client that implement ServerHandleable
     *
     * @param message the message to be received
     */
    public void update(Transmittable message) {
        ((ServerHandleable) message).handleMessage(this);
    }
}