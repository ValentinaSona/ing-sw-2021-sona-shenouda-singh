package it.polimi.ingsw.server.model.action;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientActivateProductionMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;

public class ProductionOrBuyDevAction implements Action{

    @Override
    public void handleDisconnection(Player player, Controller controller, RemoteViewHandler view, User user) {
        //doing the operation on behalf of the disconnected player
        try {
            controller.resourceController.activateProduction(new ClientActivateProductionMessage(),view,user);
        } catch (EndOfGameException e) {
            e.printStackTrace();
            controller.turnController.endOfGame();
        }
    }
    @Override
    public void handleReconnection(Player player, ServerGameReconnectionMessage message) {
        player.setGameActionEmpty();
        message.setPendingAction(true);
    }
}
