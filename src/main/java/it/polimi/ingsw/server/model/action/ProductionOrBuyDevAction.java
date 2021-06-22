package it.polimi.ingsw.server.model.action;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;

public class ProductionOrBuyDevAction implements Action{

    @Override
    public void handleReconnection(Player player, ServerGameReconnectionMessage message) {
        player.setGameActionEmpty();
        message.setPendingAction(true);
    }
}
