package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientConvertWhiteMarblesMessage implements ClientMessage, ControllerHandleable {

    private final MarketMarble[] choices;

    public ClientConvertWhiteMarblesMessage(MarketMarble[] choices){
        this.choices = choices;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        try {
            handler.marketController.convertWhiteMarbles(this, view, user);
        } catch (EndOfGameException e) {
            endOfGame(handler,e);
        }
        return  true;
    }

    public MarketMarble[] getChoices() {
        return choices;
    }

    private void endOfGame(Controller handler, EndOfGameException e) {
        handler.turnController.endOfGame(e);
    }
}
