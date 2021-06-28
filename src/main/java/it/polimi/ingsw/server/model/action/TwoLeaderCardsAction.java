package it.polimi.ingsw.server.model.action;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.WhiteMarbleAbility;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertWhiteMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;

import java.util.Arrays;


public class TwoLeaderCardsAction implements Action{
    private final int whiteMarbles;

    public TwoLeaderCardsAction(int whiteMarbles){
        this.whiteMarbles = whiteMarbles;
    }
    @Override
    public void handleDisconnection(Player player, Controller controller, RemoteViewHandler view, User user) {
        //doing the operation on behalf of the disconnected player
        MarketMarble[] marbles = new MarketMarble[whiteMarbles];
        MarketMarble choice = ((WhiteMarbleAbility) player.getLeaderCards().get(0).getSpecialAbility()).getMarble();
        Arrays.fill(marbles, choice);
        try{
            controller.marketController.convertWhiteMarbles(
                    new ClientConvertWhiteMarblesMessage(marbles),
                    view,
                    user
            );
        } catch (EndOfGameException e) {
            e.printStackTrace();
            controller.turnController.endOfGame(e);
        }
    }

    @Override
    public void handleReconnection(Player player, ServerGameReconnectionMessage message) {
        player.setGameActionEmpty();
        message.setPendingAction(true);
    }
}
