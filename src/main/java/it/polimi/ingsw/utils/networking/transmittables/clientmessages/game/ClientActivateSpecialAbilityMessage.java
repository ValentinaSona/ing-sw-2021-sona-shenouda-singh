package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientActivateSpecialAbilityMessage implements ClientMessage, ControllerHandleable {
    private final Id leaderCardId;

    public ClientActivateSpecialAbilityMessage(Id leaderCardId){
        this.leaderCardId = leaderCardId;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.leaderCardsController.activateSpecialAbility(this, view, user);
        return true;
    }

    public Id getLeaderId() {
        return leaderCardId;
    }
}
