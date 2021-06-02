package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.model.LeaderCardsKeeper;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;

import java.util.ArrayList;
import java.util.List;

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

    public void startSetupTurn(){

        Player startingPlayer = model.getCurrentPlayer();
        startingPlayer.toggleTurn();
        startingPlayer.toggleMainAction();

        //ad ogni player invio le leaderCard da cui deve scegliere e
        //le risorse iniziali che deve scegliere e di quanto è avanzato sul tracciato fede
        List<Player> players = model.getPlayers();
        LeaderCardsKeeper keeper = model.getLeaderCardsKeeper();
        int currentPlayerIdx = players.indexOf(startingPlayer);
        int resources = 0;
        int faitPoints = 0;

        if(currentPlayerIdx == 0 || currentPlayerIdx == 1){
            resources = currentPlayerIdx;
        }else if(currentPlayerIdx == 2){
            resources = 1;
            faitPoints = 1;
        }else if(currentPlayerIdx == 3){
            resources = 2;
            faitPoints = 2;
        }else{
            new RuntimeException("This should never happen");
        }


        try {
            startingPlayer.getFaithTrack().addFaithPoints(faitPoints);
        } catch (VaticanReportException e) {
            //during the setup this exception will never be thrown
            e.printStackTrace();
        }
        model.notify(new ServerSetupUserMessage(
                resources,
                startingPlayer.getVisibleFaithTrack(),
                keeper.pickFour(),
                model.getUserFromPlayer(startingPlayer)
        ));
        //adesso il primo giocatore della lista user avrà la view utilizzabile e quindi potrà
        //scegliere le risorse da usare e le leaderCard scelte
    }

    public void endSetupTurn(){
        Player endingPlayer = model.getCurrentPlayer();
        ArrayList<Player> players = model.getPlayers();
        int idx = players.indexOf(endingPlayer);

        //last player has done the setup procedure
        if(idx == players.size()-1){
            Player startingPlayer = players.get(0);
            model.setGameState(GameState.PLAY);
            startingPlayer.toggleTurn();
            startingPlayer.toggleMainAction();
            model.notify(new ServerStartTurnMessage(
                    model.getUserFromPlayer(startingPlayer),
                    model.getUserFromPlayer(endingPlayer)
            ));
        }else{
            model.setCurrentPlayer(players.get(idx+1));
            startSetupTurn();
        }

    }

}