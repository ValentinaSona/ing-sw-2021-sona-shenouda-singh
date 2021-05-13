package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientSelectDevelopmentCardMessage implements ClientMessage, ControllerHandleable {

    private final int row;
    private final int col;
    private final Id targetSlot;


    public ClientSelectDevelopmentCardMessage(int row, int col, Id slot){
        this.row = row; this.col = col; this.targetSlot = slot;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.developmentCardMarketController.selectDevelopmentCard(this, view, user);
        return  true;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Id getTargetSlot() {
        return targetSlot;
    }
}
