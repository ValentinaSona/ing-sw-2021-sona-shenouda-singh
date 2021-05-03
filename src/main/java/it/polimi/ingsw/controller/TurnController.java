package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import java.util.ArrayList;

public class TurnController extends AbstractController{

    /*  Called when a player ends their turn.
            Modifies currentPlayer and playerList for all controller and resets players' actions as needed.
        */
    public void endTurn(Player player) {
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
            getCurrentPlayer().toggleMainAction();
        }else {
            //notifico al player che non è il suo turno..
        }
    }

}