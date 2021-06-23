package it.polimi.ingsw.utils.networking.transmittables.persistenza;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class ServerGameSavingMessage implements ServerMessage, ClientHandleable{

    //TODO ..
    @Override
    public boolean handleMessage(DispatcherController handler) {
        return false;
    }
}
