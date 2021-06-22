package it.polimi.ingsw.server.model.action;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerEndLastBuyMarblesActionMessage;

public class ProductionOrBuyDevAction implements Action{

    @Override
    public void handleReconnection(Player player, Controller controller, RemoteViewHandler view) {
        player.setGameActionEmpty();
        view.updateFromGame(new ServerEndLastBuyMarblesActionMessage());
    }
}
