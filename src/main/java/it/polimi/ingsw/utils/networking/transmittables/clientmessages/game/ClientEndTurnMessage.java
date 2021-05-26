package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientEndTurnMessage implements ClientMessage, ControllerHandleable {


    public ClientEndTurnMessage(){

    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.turnController.endTurn(this,view, user);
        return  true;
    }

}