package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerStartSetupTurnMessage implements ServerMessage, ClientHandleable {
    private final User startingTurn;
    private final MarketView marketView;
    private final DevMarketView devMarketView;;

    public ServerStartSetupTurnMessage(User startingTurn, MarketView marketView, DevMarketView devMarketView){
        this.startingTurn = startingTurn;
        this.marketView = marketView;
        this.devMarketView = devMarketView;
    }

    public User getStartingTurn() {
        return startingTurn;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
