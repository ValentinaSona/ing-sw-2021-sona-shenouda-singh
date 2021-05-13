package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientThrowResourcesMessage implements ClientMessage, ControllerHandleable {


    public  ClientThrowResourcesMessage(){

        }

        @Override
        public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
            handler.resourceController.throwResources( view, user);
            return  true;
        }



}
