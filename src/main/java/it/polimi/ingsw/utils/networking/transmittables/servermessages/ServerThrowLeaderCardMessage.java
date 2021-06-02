package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerThrowLeaderCardMessage implements ServerMessage, ClientHandleable {
    private final LeaderCard leaderCard;
    private final User user;

    public ServerThrowLeaderCardMessage(LeaderCard leaderCard, User user) {
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
    public boolean handleMessage(DispatcherController handler) {
        return false;
    }
}
