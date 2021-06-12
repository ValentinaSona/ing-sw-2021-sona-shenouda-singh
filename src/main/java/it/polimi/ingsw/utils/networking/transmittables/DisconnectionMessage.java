package it.polimi.ingsw.utils.networking.transmittables;


import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class DisconnectionMessage implements ClientHandleable, ControllerHandleable, ServerHandleable, ClientMessage, ServerMessage {

    //TODO
    @Override
    public boolean handleMessage(DispatcherController handler) {

        return false;
    }

    //TODO
    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        return false;
    }

    //TODO
    @Override
    public boolean handleMessage(ConnectionSetupHandler handler) {
        handler.getLobby().handleLobbyDisconnection(handler.getConnection());
        return true;
    }
}
