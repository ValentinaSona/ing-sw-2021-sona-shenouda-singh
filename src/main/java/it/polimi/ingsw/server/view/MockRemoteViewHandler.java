package it.polimi.ingsw.server.view;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.DisconnectionMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MockRemoteViewHandler extends RemoteViewHandler{
    private Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private UIController uiController;
    private DispatcherController dispatcherController;

    public MockRemoteViewHandler(String nickname, UIController uiController, DispatcherController dispatcherController){
        super(nickname);
        this.dispatcherController = dispatcherController;
        this.uiController = uiController;
    }

    @Override
    public void updateFromClient(DisconnectionMessage message) {
        //in a local Game this method should not be called
        updateFromClient((Transmittable) message);
    }

    @Override
    public void updateFromGame(Transmittable message) {
        LOGGER.log(Level.INFO,"Ho ricevuto il messaggio dal controller e lo sto inviando al dispatcher");
        dispatcherController.update(message);
    }

    @Override
    public void requestDisconnection() {
        //in a local Game this method should not be called
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {
        dispatcherController.handleStatus(message);
    }
}
