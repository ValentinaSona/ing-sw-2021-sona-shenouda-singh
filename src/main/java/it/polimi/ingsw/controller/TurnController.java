package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class TurnController extends AbstractController{


/* Observer method, uncomment once the relevant classes are implemented
       Checks that it is the turn of the player issuing the action,
       Then calls the appropriate method for the action called.
     */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    /*  Called when a player ends their turn.
            Modifies currentPlayer and playerList for all controller and resets players' actions as needed.
        */
    private void endTurn(Player player) {
        if(player.equals(getCurrentPlayer())){
            player.toggleTurn();
            ArrayList<Player> players = getPlayersList();
            int idx = players.indexOf((Player) player);

            if(idx == players.size()-1){
                //restart from the first player in the list
                setCurrentPlayer(players.get(0));
            }else{
                setCurrentPlayer(players.get(idx+1));
            }

            getCurrentPlayer().toggleTurn();
            getCurrentPlayer().resetAction();

        }else {
            //notifico al player che non è il suo turno..
        }
    }

}