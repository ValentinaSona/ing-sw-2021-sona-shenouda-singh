package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientThrowLeaderCardMessage  implements ClientMessage, ControllerHandleable {
    private final Id leaderCardId;

    public ClientThrowLeaderCardMessage(Id leaderCardId){
        this.leaderCardId = leaderCardId;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        try {
            handler.leaderCardsController.throwLeaderCard(this, view, user);
        } catch (EndOfGameException e) {
            endOfGame(handler, e);
        }
        return true;
    }

    public Id getLeaderId() {
        return leaderCardId;
    }

    private void endOfGame(Controller handler, EndOfGameException e) {
        handler.turnController.endOfGame(e);
    }
}
