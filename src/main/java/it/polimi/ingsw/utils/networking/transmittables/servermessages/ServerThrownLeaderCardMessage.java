package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerThrownLeaderCardMessage implements ServerMessage, ClientHandleable {
    private final LeaderCard leaderCard;
    private final User user;

    public ServerThrownLeaderCardMessage(LeaderCard leaderCard, User user) {
        this.user = user;
        this.leaderCard = leaderCard;
    }

    public LeaderCard getLeaderCard() {
        return leaderCard;
    }

    public User getUser() {
        return user;
    }

    //TODO
    @Override
    public boolean handleMessage(UiControllerInterface handler) {
        return false;
    }
}
