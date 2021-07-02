package it.polimi.ingsw.utils.networking.transmittables.resilienza;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

import java.util.List;

public class ServerGameReconnectionMessage implements ServerMessage, ClientHandleable {
    private boolean pendingAction;
    private final List<PlayerView> playerViews;
    private final PlayerView currentPlayer;
    private final MarketView marketView;
    private final DevMarketView devMarketView;
    private final int blackCross;


    public ServerGameReconnectionMessage(boolean pendingAction, List<PlayerView> playerViews, PlayerView currentPlayer, MarketView marketView, DevMarketView devMarketView, int blackCross) {
        this.pendingAction = pendingAction;
        this.playerViews = playerViews;
        this.currentPlayer = currentPlayer;
        this.marketView = marketView;
        this.devMarketView = devMarketView;
        this.blackCross = blackCross;
    }



    public void setPendingAction(boolean pendingAction){
        this.pendingAction =pendingAction;
    }
    public boolean isPendingAction() {
        return pendingAction;
    }

    public PlayerView getCurrentPlayer() {
        return currentPlayer;
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

    public int getBlackCross() { return blackCross; }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleGameReconnection(this);
        return true;
    }
}
