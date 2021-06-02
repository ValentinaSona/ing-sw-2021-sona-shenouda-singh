package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.ui.controller.LobbyMenuController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
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

    //TODO
    @Override
    public boolean handleMessage(UiControllerInterface handler) {
        try{
            ((LobbyMenuController) handler).handleSetupGameMessage(this);
            return true;
        }catch (ClassCastException e){
            //this should never happen
            e.printStackTrace();
            return false;
        }
    }
}
