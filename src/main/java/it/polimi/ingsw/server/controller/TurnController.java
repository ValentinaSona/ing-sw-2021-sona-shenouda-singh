package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.LobbyState;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.util.*;

public class TurnController{
    private static TurnController singleton;
    private Game model;
    private Controller controller;

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

    public static TurnController destroy(){
        if(singleton != null){
            singleton = null;
        }

        return null;
    }

    /**
     * Called when the user communicates that their turn has ended.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void endTurn(ClientEndTurnMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

        Player endingPlayer = model.getPlayerFromUser(user);

        if( !(endingPlayer.getTurn())  || endingPlayer.getMainAction() ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        }else{
            endingPlayer.toggleTurn();


            List<Player> players = model.getPlayers();
            int startingIdx = players.indexOf(endingPlayer);

            int idx =(startingIdx == players.size()-1)? 0: startingIdx+1;
            while (true){
                Player p = model.getPlayers().get(idx);

                if(!p.isDisconnected()){
                    model.setCurrentPlayer(players.get(idx));
                    break;
                }
                idx =(idx == players.size()-1)? 0: idx+1;
            }


            if (model.isSolo()) {
                LorenzoAction();
            }

            //TODO metto boolean per dire se è un giocatore
            //TODO che si è disconnesso e anche quello di riconnessione
            Player startingPlayer = model.getCurrentPlayer();
            startingPlayer.toggleTurn();
            startingPlayer.toggleMainAction();
            model.notify(new ServerStartTurnMessage(
                    model.getUserFromPlayer(startingPlayer),
                    model.getUserFromPlayer(endingPlayer)
            ));

        }

    }

    public void forceEndTurn(Player endingPlayer){

        endingPlayer.toggleTurn();
        if(endingPlayer.getMainAction()){
            endingPlayer.toggleMainAction();
        }
        endingPlayer.setDisconnected(true);

        if (model.isSolo()) {
            try {
                LorenzoAction();
            } catch (EndOfGameException e) {
                endOfGame(e);
            }
        }

        if(model.isSolo()){
            model.setGameState(GameState.WAITING_FOR_SOMEONE);
        }else if(model.getPlayers().stream().anyMatch(p-> !p.isDisconnected())){
            ArrayList<Player> players = model.getPlayers();
            int startingIdx = players.indexOf(endingPlayer);

            int idx =(startingIdx == players.size()-1)? 0: startingIdx+1;
            while (true){
                Player p = model.getPlayers().get(idx);

                if(!p.isDisconnected()){
                    model.setCurrentPlayer(players.get(idx));
                    break;
                }
                idx =(idx == players.size()-1)? 0: idx+1;
            }

        }else{
            model.setGameState(GameState.WAITING_FOR_SOMEONE);
        }

        if(model.getGameState().equals(GameState.WAITING_FOR_SOMEONE)){
            return;
        }

        Player startingPlayer = model.getCurrentPlayer();
        startingPlayer.toggleTurn();
        startingPlayer.toggleMainAction();
        model.notify(new ServerStartTurnMessage(
                model.getUserFromPlayer(startingPlayer),
                model.getUserFromPlayer(endingPlayer)
        ));

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
            faithPoints = 1;
        }else{
            throw new RuntimeException("This should never happen");
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

    }

    public void endSetupTurn(){
        Player endingPlayer = model.getCurrentPlayer();
        ArrayList<Player> players = model.getPlayers();
        int idx = players.indexOf(endingPlayer);

        endingPlayer.toggleTurn();
        endingPlayer.toggleMainAction();

        //last player has done the setup procedure
        if(idx == players.size()-1) {
            Player startingPlayer = players.get(0);
            model.setGameState(GameState.PLAY);

            if(!controller.getLocal()){
                controller.setLobbyState(LobbyState.IN_GAME);
            }

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
                    model.notify(new ServerSoloMoveMessage(model.getLorenzo().getBlackCross(), false));
                } catch (VaticanReportException e) {
                    Player player = model.getPlayers().get(0);
                    player.getFaithTrack().validatePopeFavor(e.getReport());
                    model.getLorenzo().getFaithTrack().validatePopeFavor(e.getReport());
                    model.notify( new ServerFaithTrackMessage(true, player.getVisibleFaithTrack(), 2, null ) );

                    if (e.getReport()==3) throw new EndOfGameException(EndOfGameCause.LORENZO_FAITH);

                }

            }

            case MOVE_SHUFFLE -> {
                try {
                    model.getLorenzo().getFaithTrack().addFaithPoints(1);
                } catch (VaticanReportException e) {
                    Player player = model.getPlayers().get(0);
                    player.getFaithTrack().validatePopeFavor(e.getReport());
                    model.getLorenzo().getFaithTrack().validatePopeFavor(e.getReport());

                    // This null needs to be handled client side but shouldn't be a problem as it's expected of singleplayer.
                    model.notify( new ServerFaithTrackMessage( true, player.getVisibleFaithTrack(), 1,null ) );
                    if (e.getReport()==3) throw new EndOfGameException(EndOfGameCause.LORENZO_FAITH);
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
     * @param e
     */
    public void endOfGame(EndOfGameException e){



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
                if (scores.containsKey(model.getUserFromPlayer(player)) && scores.get(model.getUserFromPlayer(player)) >= highest.getValue()){
                    highest = new AbstractMap.SimpleEntry<User,Integer>(model.getUserFromPlayer(player),scores.get(model.getUserFromPlayer(player)));
                }
            }
            scores.remove(highest.getKey());
            rank.put(highest.getKey(),highest.getValue());
        }

        model.notify(new ServerFinalScoreMessage(rank, e.getEndCause()));

    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}