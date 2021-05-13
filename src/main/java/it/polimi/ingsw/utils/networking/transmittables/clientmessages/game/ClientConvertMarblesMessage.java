package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientConvertMarblesMessage implements ClientMessage, ControllerHandleable {

    private final MarketMarble[] marbles;

    public ClientConvertMarblesMessage(MarketMarble[] marbles){
        this.marbles = marbles;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.marketController.convertMarbles(this, view, user);
        return  true;
    }

    public MarketMarble[] getMarbles() {
        return marbles;
    }
}
