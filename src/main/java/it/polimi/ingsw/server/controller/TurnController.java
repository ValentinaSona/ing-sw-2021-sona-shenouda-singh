package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.util.*;

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
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void endTurn(ClientEndTurnMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

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

            if (model.isSolo()) {
                    LorenzoAction();
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
        int faithPoints = 0;

        if(currentPlayerIdx == 0 || currentPlayerIdx == 1){
            resources = currentPlayerIdx;
        }else if(currentPlayerIdx == 2){
            resources = 1;
            faithPoints = 1;
        }else if(currentPlayerIdx == 3){
            resources = 2;
            faithPoints = 2;
        }else{
            new RuntimeException("This should never happen");
        }


        try {
            startingPlayer.getFaithTrack().addFaithPoints(faithPoints);
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
        //TODO: Doesn't send third one?
        Player endingPlayer = model.getCurrentPlayer();
        ArrayList<Player> players = model.getPlayers();
        int idx = players.indexOf(endingPlayer);

        endingPlayer.toggleTurn();
        endingPlayer.toggleMainAction();

        //last player has done the setup procedure
        if(idx == players.size()-1){
            Player startingPlayer = players.get(0);
            model.setGameState(GameState.PLAY);
            startingPlayer.toggleTurn();
            startingPlayer.toggleMainAction();
            model.setCurrentPlayer(startingPlayer);
            model.notify(new ServerStartTurnMessage(
                    model.getUserFromPlayer(startingPlayer),
                    model.getUserFromPlayer(endingPlayer)
            ));
        }else{
            model.setCurrentPlayer(players.get(idx+1));
            startSetupTurn();
        }

    }

    /**
     * Performs a random solo action.
     * @throws EndOfGameException if Lorenzo wins.
     */
    private void LorenzoAction() throws EndOfGameException {
        switch (model.getLorenzo().pop()){
            case MOVE -> {
                try {
                    model.getLorenzo().getFaithTrack().addFaithPoints(2);
                } catch (VaticanReportException e) {
                    Player player = model.getPlayers().get(0);
                    player.getFaithTrack().validatePopeFavor(e.getReport());

                    // This null needs to be handled client side but shouldn't be a problem as it's expected of singleplayer.
                    model.notify( new ServerFaithTrackMessage( player.getVisibleFaithTrack(), 2, null ) );

                    if (e.getReport()==3) throw new EndOfGameException(true);

                }
                model.notify(new ServerSoloMoveMessage(model.getLorenzo().getBlackCross(), false));
            }

            case MOVE_SHUFFLE -> {
                try {
                    model.getLorenzo().getFaithTrack().addFaithPoints(1);
                } catch (VaticanReportException e) {
                    Player player = model.getPlayers().get(0);
                    player.getFaithTrack().validatePopeFavor(e.getReport());

                    // This null needs to be handled client side but shouldn't be a problem as it's expected of singleplayer.
                    model.notify( new ServerFaithTrackMessage( player.getVisibleFaithTrack(), 1,null ) );

                }
                model.getLorenzo().shuffle();

                model.notify(new ServerSoloMoveMessage(model.getLorenzo().getBlackCross(), true));
            }
            case DISCARD_BLUE -> {
                model.getDevelopmentCardsMarket().discard(DevelopmentType.BLUE);
                model.getDevelopmentCardsMarket().discard(DevelopmentType.BLUE);
                model.notify(new ServerSoloDiscardMessage(model.getDevelopmentCardsMarket().getVisible(), DevelopmentType.BLUE));
            }
            case DISCARD_GREEN -> {
                model.getDevelopmentCardsMarket().discard(DevelopmentType.GREEN);
                model.getDevelopmentCardsMarket().discard(DevelopmentType.GREEN);
                model.notify(new ServerSoloDiscardMessage(model.getDevelopmentCardsMarket().getVisible(), DevelopmentType.GREEN));
            }
            case DISCARD_PURPLE -> {
                model.getDevelopmentCardsMarket().discard(DevelopmentType.PURPLE);
                model.getDevelopmentCardsMarket().discard(DevelopmentType.PURPLE);
                model.notify(new ServerSoloDiscardMessage(model.getDevelopmentCardsMarket().getVisible(), DevelopmentType.PURPLE));
            }
            case DISCARD_YELLOW -> {
                model.getDevelopmentCardsMarket().discard(DevelopmentType.YELLOW);
                model.getDevelopmentCardsMarket().discard(DevelopmentType.YELLOW);
                model.notify(new ServerSoloDiscardMessage(model.getDevelopmentCardsMarket().getVisible(), DevelopmentType.YELLOW));

            }
        }

    }

    /**
     * Called when the game is done.
     * @param view The view.
     */
    public void endOfGame(RemoteViewHandler view){

        Map<User, Integer> scores = new LinkedHashMap<>();

        for (Player player : model.getPlayers()){
            scores.put(model.getUserFromPlayer(player), player.getVictoryPoints() );
        }

        Map<User, Integer> rank = new LinkedHashMap<>();

        Map.Entry<User,Integer> highest = new AbstractMap.SimpleEntry<User,Integer>(null,0);
        int size = scores.size();

        while(rank.size()< size){
            highest.setValue(0);
            for (Player player: model.getPlayers()){
                if (scores.get(model.getUserFromPlayer(player)) > highest.getValue()){
                    highest = new AbstractMap.SimpleEntry<User,Integer>(model.getUserFromPlayer(player),scores.get(model.getUserFromPlayer(player)));
                }
            }
            scores.remove(highest.getKey());
            rank.put(highest.getKey(),highest.getValue());
        }

        model.notify(new ServerFinalScoreMessage(rank));

    }
}