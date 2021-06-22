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
    private boolean pendingAction;
    private final List<PlayerView> playerViews;
    private final MarketView marketView;
    private final DevMarketView devMarketView;


    public ServerGameReconnectionMessage(boolean pendingAction, List<PlayerView> playerViews, MarketView marketView, DevMarketView devMarketView) {
        this.pendingAction = pendingAction;
        this.playerViews = playerViews;
        this.marketView = marketView;
        this.devMarketView = devMarketView;
    }

    public void setPendingAction(boolean pendingAction){
        this.pendingAction =pendingAction;
    }
    public boolean isPendingAction() {
        return pendingAction;
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

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleGameReconnection(this);
        return true;
    }
}
