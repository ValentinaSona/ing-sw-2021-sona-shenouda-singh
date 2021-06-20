package it.polimi.ingsw.utils.networking.transmittables.resilienza;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class ServerEndLastBuyMarblesActionMessage implements ServerMessage, ClientHandleable {

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleEndLastBuyMarblesAction(this);
        return true;
    }
}
