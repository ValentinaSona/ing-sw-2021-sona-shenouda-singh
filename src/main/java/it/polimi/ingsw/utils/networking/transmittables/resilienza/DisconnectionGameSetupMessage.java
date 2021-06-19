package it.polimi.ingsw.utils.networking.transmittables.resilienza;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class DisconnectionGameSetupMessage implements ClientHandleable, ServerMessage {

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleDisconnectionGameSetup(this);
        return true;
    }
}
