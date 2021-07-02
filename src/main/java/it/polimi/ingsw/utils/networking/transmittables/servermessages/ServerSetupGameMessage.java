package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerSetupGameMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<User> users;
    private final MarketView marketView;
    private final DevMarketView devMarketView;

    public ServerSetupGameMessage(ArrayList<User> users, MarketView marketView, DevMarketView devMarketView){
        this.users = users;
        this.marketView = marketView;
        this.devMarketView = devMarketView;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public DevMarketView getDevMarketView() {
        return devMarketView;
    }

    public MarketView getMarketView() {
        return marketView;
    }


    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleSetupGame(this);
        return true;
    }
}
