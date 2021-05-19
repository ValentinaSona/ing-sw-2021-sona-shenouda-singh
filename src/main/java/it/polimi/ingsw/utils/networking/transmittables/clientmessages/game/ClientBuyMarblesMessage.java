package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientBuyMarblesMessage implements ClientMessage, ControllerHandleable {
    private final int rowCol;

    public ClientBuyMarblesMessage(int rowCol){
        this.rowCol = rowCol;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.marketController.buyMarbles(this, view, user);
        return  true;
    }

    public int getRowCol() {
        return rowCol;
    }
}

