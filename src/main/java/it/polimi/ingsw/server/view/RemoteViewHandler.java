package it.polimi.ingsw.server.view;

import it.polimi.ingsw.server.controller.User;

import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.DisconnectionMessage;
import it.polimi.ingsw.utils.observer.LambdaObservable;
import it.polimi.ingsw.utils.observer.LambdaObserver;

public abstract class RemoteViewHandler extends LambdaObservable<ViewClientMessage> implements LambdaObserver {
    private final User user;


    /**
     * Constructor of the view.
     * @param nickname   the nickname of the User owning the view
     */

    public RemoteViewHandler(String nickname) {
        this.user = new User(nickname);
    }
    /**
     * Handler of a message coming from the client, this method gets called by the connection
     * that the RealRemoteViewHandler observe when the connection receive a ClientMessage and the RealRemoteViewHandler
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
    public abstract void updateFromClient(DisconnectionMessage message);

    /**
     * Handler of a message coming from the game.
     *
     * @param message a message coming from the game
     */
    public abstract void updateFromGame(Transmittable message);

    /**
     * Handler of a disconnect message coming from the game.
     */
    public abstract void requestDisconnection();

    /**
     * Handler of a status message.
     *
     * @param message a message coming from the controller
     */
    public abstract void handleStatusMessage(StatusMessage message);

    /**
     * Getter of the user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
