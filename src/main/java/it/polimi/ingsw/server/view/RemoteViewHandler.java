package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.observer.LambdaObservable;
import it.polimi.ingsw.utils.observer.LambdaObserver;

/**
 * This class is bound to a specific connection and observes both connection in order
 * to be notified when the model evolves or the client sends a new message
 */
public class RemoteViewHandler extends LambdaObservable<ViewClientMessage> implements LambdaObserver {

    /**
     * The user owning the view.
     */
    private final User user;

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

    public RemoteViewHandler(Connection connection, String nickname) {
        this.connection = connection;
        connection.addObserver(
                this,
                (lambdaObserver, transmittable) ->
                {
                    if (transmittable instanceof DisconnectionMessage) {
                        ((RemoteViewHandler) lambdaObserver).updateFromClient((DisconnectionMessage) transmittable);
                    } else {
                        ((RemoteViewHandler) lambdaObserver).updateFromClient(transmittable);
                    }
                }
        );
        this.user = new User(nickname);
    }
    /**
     * Handler of a message coming from the client, this method gets called by the connection
     * that the RemoteViewHandler observe when the connection receive a ClientMessage and the RemoteViewHandler
     * wrap this ClientMessage in a ViewClientMessage and passes it to the controller
     *
     * @param message a message coming from the client
     */
    public void updateFromClient(Transmittable message) {
        this.notify(
                new ViewClientMessage((ClientMessage) message, this, this.getUser())
        );
    }

    /**
     * Handler of a disconnect message coming from the client.
     *
     * @param message a message coming from the client
     */
    public void updateFromClient(DisconnectionMessage message) {
        connection.close();
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
        connection.close();
    }

    /**
     * Handler of a status message.
     *
     * @param message a message coming from the controller
     */
    public void handleStatusMessage(StatusMessage message) {
        this.connection.send(message);
    }

    /**
     * Getter of the user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }


}
