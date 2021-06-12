package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.DisconnectionMessage;
import it.polimi.ingsw.utils.observer.LambdaObservable;
import it.polimi.ingsw.utils.observer.LambdaObserver;

/**
 * This class is bound to a specific connection and observes both connection in order
 * to be notified when the model evolves or the client sends a new message
 */
public class RealRemoteViewHandler extends RemoteViewHandler {
    /**
     * The connection to the client.
     */
    private final Connection connection;

    /**
     * Constructor of the view.
     *
     * @param connection the connection inherent to the view
     * @param nickname   the nickname of the User owning the view
     */

    public RealRemoteViewHandler(Connection connection, String nickname) {
        super(nickname);
        this.connection = connection;
        connection.addObserver(
                this,
                (lambdaObserver, transmittable) ->
                {
                    if (transmittable instanceof DisconnectionMessage) {
                        ((RealRemoteViewHandler) lambdaObserver).updateFromClient((DisconnectionMessage) transmittable);
                    } else {
                        ((RealRemoteViewHandler) lambdaObserver).updateFromClient(transmittable);
                    }
                }
        );
    }


    /**
     * Handler of a disconnect message coming from the client.
     *
     * @param message a message coming from the client
     */
    public void updateFromClient(DisconnectionMessage message) {
        connection.closeConnection();
        this.updateFromClient((Transmittable) message);
    }

    /**
     * Handler of a message coming from the game.
     *
     * @param message a message coming from the game
     */
    public void updateFromGame(Transmittable message) {
        this.connection.send(message);
    }

    /**
     * Handler of a disconnect message coming from the game.
     */
    public void requestDisconnection() {
        connection.closeConnection();
    }

    /**
     * Handler of a status message.
     *
     * @param message a message coming from the controller
     */
    public void handleStatusMessage(StatusMessage message) {
        this.connection.send(message);
    }

}
