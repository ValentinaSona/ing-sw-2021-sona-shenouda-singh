package it.polimi.ingsw.utils.networking.transmittables.resilienza;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

import java.util.List;

public class ServerGameReconnectionMessage implements ServerMessage, ClientHandleable {
    private final User currentUser;
    private final List<PlayerView> playerViews;
    private final MarketView marketView;
    private final DevMarketView devMarketView;


    public ServerGameReconnectionMessage(User currentUser, List<PlayerView> playerViews, MarketView marketView, DevMarketView devMarketView) {
        this.currentUser = currentUser;
        this.playerViews = playerViews;
        this.marketView = marketView;
        this.devMarketView = devMarketView;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<PlayerView> getPlayerViews() {
        return playerViews;
    }

    public MarketView getMarketView() {
        return marketView;
    }

    public DevMarketView getDevMarketView() {
        return devMarketView;
    }

    //TODO ..
    @Override
    public boolean handleMessage(DispatcherController handler) {
        return false;
    }
}
