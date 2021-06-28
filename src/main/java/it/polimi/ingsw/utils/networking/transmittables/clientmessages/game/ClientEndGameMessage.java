package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

/**
 * Message created for testing purpose - automatically triggers end of game if called.
 */
public class ClientEndGameMessage implements ClientMessage, ControllerHandleable {


    public ClientEndGameMessage(){
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        handler.turnController.endOfGame(new EndOfGameException(EndOfGameCause.DEBUG));
        return  true;
    }

}