package it.polimi.ingsw.utils.networking.transmittables.persistenza;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientSaveGameMessage implements ClientMessage, ControllerHandleable {

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        handler.saveToFile(this, view, user);
        return true;
    }
}
