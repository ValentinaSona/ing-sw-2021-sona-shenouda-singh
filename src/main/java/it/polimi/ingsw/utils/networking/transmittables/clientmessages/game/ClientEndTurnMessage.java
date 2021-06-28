package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientEndTurnMessage implements ClientMessage, ControllerHandleable {


    public ClientEndTurnMessage(){

    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        try {
            handler.turnController.endTurn(this,view, user);
        } catch (EndOfGameException e) {
            endOfGame(handler, e);
            return false;
        }
        return  true;
    }

    private void endOfGame(Controller handler, EndOfGameException e) {
        handler.turnController.endOfGame(e);
    }

}