package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class ServerLobbyReconnectionMessage implements ServerMessage, ClientHandleable {
    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleLobbyReconnection(this);
        return true;
    }
}
