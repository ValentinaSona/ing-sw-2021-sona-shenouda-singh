package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.observable.Player;
import java.util.ArrayList;

public class TurnController extends AbstractController{
    private static TurnController singleton;

    /*  Called when a player ends their turn.
            Modifies currentPlayer and playerList for all controller and resets players' actions as needed.
        */
    private TurnController(Model model){
        super(model);
    }
    public static TurnController getInstance(Model model){
        if(singleton == null){
            singleton = new TurnController(model);
        }

        return singleton;
    }

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
          //  player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }
    }

}