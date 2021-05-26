package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientActivateProductionMessage implements ClientMessage, ControllerHandleable {


    public ClientActivateProductionMessage(){

    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.resourceController.activateProduction(this,view, user);
        return  true;
    }

}