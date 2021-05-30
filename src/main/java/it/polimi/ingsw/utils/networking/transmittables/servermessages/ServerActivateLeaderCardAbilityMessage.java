package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerActivateLeaderCardAbilityMessage implements ServerMessage, ClientHandleable {
    private final LeaderCard ability;
    private final User user;

    public ServerActivateLeaderCardAbilityMessage(LeaderCard targetCard, User user) {
        this.ability = targetCard;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public LeaderCard getAbility() {
        return ability;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
