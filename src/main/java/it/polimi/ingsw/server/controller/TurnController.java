package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

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

    /** TODO: should also be called when timeout kicks in in case of disconnection.
     * Called when the user communicates that their turn has ended.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void endTurn(RemoteViewHandler view, User user) {

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn())  ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        }else{
                player.toggleTurn();

                ArrayList<Player> players = getPlayersList();
                int idx = players.indexOf(player);

                if(idx == players.size()-1){
                    //restart from the first player in the list
                    setCurrentPlayer(players.get(0));
                }else{
                    setCurrentPlayer(players.get(idx+1));
                }

                getCurrentPlayer().toggleTurn();
                getCurrentPlayer().toggleMainAction();

        }

    }

}