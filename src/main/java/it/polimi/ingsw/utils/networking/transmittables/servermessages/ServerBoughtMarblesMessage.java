package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.FaithTrackView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.FaithTrack;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerBoughtMarblesMessage implements ServerMessage, ClientHandleable {
    private final MarketView marketView;
    private final ArrayList<Resource> boughtResources;
    private final User user;

    public ServerBoughtMarblesMessage(MarketView marketView, ArrayList<Resource> boughtResources, User user){
        this.marketView = marketView;
        this.boughtResources = boughtResources;
        this.user = user;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleBoughtMarbles(this);
        return true;
    }

    public MarketView getMarketView() {
        return marketView;
    }

    public ArrayList<Resource> getBoughtResources() {
        return boughtResources;
    }


    public User getUser() {
        return user;
    }
}
