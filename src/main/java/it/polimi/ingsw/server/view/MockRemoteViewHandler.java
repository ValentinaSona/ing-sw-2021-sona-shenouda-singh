package it.polimi.ingsw.server.view;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View used in a Local solo Game and it mocks the existence of the network layer in order to maintain the
 * same protocol used for the remote Game
 */
public class MockRemoteViewHandler extends RemoteViewHandler{
    private final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    /**
     * Dispatcher for receiving actionsResult
     */
    private DispatcherController dispatcherController;

    /**
     * Constructor of the view
     * @param nickname the nickname of the user owning the view
     * @param dispatcherController used to handle incoming messages from the controller
     */
    public MockRemoteViewHandler(String nickname,DispatcherController dispatcherController){
        super(nickname);
        this.dispatcherController = dispatcherController;
    }

    /**
     * Handler of a disconnect message coming from the client,
     * in a local Game this method should not be called.
     *
     * @param message a message coming from the client
     */
    @Override
    public void updateFromClient(DisconnectionMessage message) {
        updateFromClient((Transmittable) message);
    }

    /**
     * Handler of a message coming from the game.
     *
     * @param message a message coming from the game
     */
    @Override
    public void updateFromGame(Transmittable message) {
        LOGGER.log(Level.INFO,"I have received this message and I'm forwarding it to the dispatcher:" + message);
        dispatcherController.update(message);
    }

    /**
     * Handler of a disconnect message coming from the game,
     * in a local Game this method should not be called.
     */
    @Override
    public void requestDisconnection() {
    }

    /**
     * Handler of a status message.
     *
     * @param message a message coming from the controller
     */
    @Override
    public void handleStatusMessage(StatusMessage message) {
        dispatcherController.handleStatus(message);
    }
}
