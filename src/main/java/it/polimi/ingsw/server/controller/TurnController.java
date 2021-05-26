package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;

import java.util.ArrayList;

public class TurnController{
    private static TurnController singleton;
    private Game model;

    /*  Called when a player ends their turn.
            Modifies currentPlayer and playerList for all controller and resets players' actions as needed.
        */
    private TurnController(Game model){
        this.model = model;
    }
    public static TurnController getInstance(Game model){
        if(singleton == null){
            singleton = new TurnController(model);
        }

        return singleton;
    }

    /** TODO: should also be called when timeout kicks in in case of disconnection.
     * Called when the user communicates that their turn has ended.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void endTurn(ClientEndTurnMessage action, RemoteViewHandler view, User user) {

        Player endingPlayer = model.getPlayerFromUser(user);

        if( !(endingPlayer.getTurn())  ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        }else{
            endingPlayer.toggleTurn();

            ArrayList<Player> players = model.getPlayers();
            int idx = players.indexOf(endingPlayer);

            if(idx == players.size()-1){
                //restart from the first player in the list
                model.setCurrentPlayer(players.get(0));
            }else{
                model.setCurrentPlayer(players.get(idx+1));
            }

            Player startingPlayer = model.getCurrentPlayer();
            startingPlayer.toggleTurn();
            startingPlayer.toggleMainAction();
            model.notify(new ServerStartTurnMessage(
                    model.getUserFromPlayer(startingPlayer),
                    model.getUserFromPlayer(endingPlayer)
            ));

        }

    }

}