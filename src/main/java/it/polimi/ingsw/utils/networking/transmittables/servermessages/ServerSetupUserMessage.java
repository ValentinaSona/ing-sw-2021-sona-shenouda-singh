package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.FaithTrackView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerSetupUserMessage implements ServerMessage, ClientHandleable {
    private final Integer resources;
    private final FaithTrackView faithTrackView;
    private final LeaderCard[] leaderCards;
    private final User user;

    public ServerSetupUserMessage(Integer resources, FaithTrackView faithTrackView, LeaderCard[] leaderCards, User user){
        this.leaderCards = leaderCards;
        this.resources = resources;
        this.user = user;
        this.faithTrackView = faithTrackView;
    }

    public User getUser() {
        return user;
    }

    public LeaderCard[] getLeaderCards() {
        return leaderCards;
    }

    public Integer getResources() {
        return resources;
    }

    public FaithTrackView getFaithTrackView() {
        return faithTrackView;
    }

    //TODO

    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
