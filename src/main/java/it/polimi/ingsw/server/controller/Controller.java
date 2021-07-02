package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.server.LobbyState;
import it.polimi.ingsw.server.Match;
import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.action.Action;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.server.view.ViewClientMessage;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.persistenza.ClientSaveGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.persistenza.ServerGameSavingMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import it.polimi.ingsw.utils.observer.LambdaObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main controller. References all the sub controllers and dispatches the messages to them.
 */
public class Controller implements LambdaObserver {
    private final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static Controller singleton;
    public final DevelopmentCardMarketController developmentCardMarketController;
    public final LeaderCardsController leaderCardsController;
    public final MarketController marketController;
    public final ResourceController resourceController;
    public final TurnController turnController;
    private final Game model;
    private final Match match;
    private final boolean local;
    /**
     * The message processing queue
     */
    private final BlockingQueue<ViewClientMessage> actionToProcess = new LinkedBlockingDeque<>();

    public static Controller getInstance(Game model,Match matchInstance){
        if(singleton == null){
            singleton = new Controller(model, matchInstance, false);
        }

        return singleton;
    }

    public static Controller destroy(){
        if(singleton != null){
            singleton = null;
            DevelopmentCardMarketController.destroy();
            LeaderCardsController.destroy();
            MarketController.destroy();
            ResourceController.destroy();
            TurnController.destroy();
        }

        return  null;
    }

    private Controller(Game modelInstance, Match matchInstance, boolean localGame){
        local = localGame;
        match = matchInstance;
        model = modelInstance;
        developmentCardMarketController = DevelopmentCardMarketController.getInstance(model);
        leaderCardsController = LeaderCardsController.getInstance(model);
        marketController = MarketController.getInstance(model);
        resourceController = ResourceController.getInstance(model);
        turnController = TurnController.getInstance(model);
        turnController.setController(this);
    }

    public static Controller getInstance(Game model){
        if(singleton == null){
            singleton = new Controller(model, null, true);
        }

        return singleton;
    }
    /**
     * This method is called by the views and adds the action to the processing queue
     */
    public void update(ViewClientMessage action){
        try{
            this.actionToProcess.put(action);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

    }

    public void dispatchViewClientMessage(){
        try{
            ViewClientMessage action = this.actionToProcess.take();
            ControllerHandleable handleable = (ControllerHandleable) action.clientMessage;

            if(action.user.equals(model.getUserFromPlayer(model.getCurrentPlayer())) &&
                    !(handleable instanceof DisconnectionMessage)){
                model.getCurrentPlayer().setGameActionEmpty();
            }
            handleable.handleMessage(this, action.view, action.user);

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }catch(ClassCastException classCastException){
            classCastException.printStackTrace();
            LOGGER.log(Level.SEVERE, classCastException.getMessage());
        }

    }

    public void setup(){
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Player> players = model.getPlayers();

        for(Player p: players) {
            users.add(model.getUserFromPlayer(p));
        }
        LOGGER.log(Level.INFO, "Ho mandato il messaggio di setup del game");
        model.notify(new ServerSetupGameMessage(
                users,
                model.getMarketInstance().getVisible(),
                model.getDevelopmentCardsMarket().getVisible()
        ));

        model.setCurrentPlayer(players.get(0));
        turnController.startSetupTurn();
    }

    public boolean getLocal(){
        return local;
    }

    /**
     * Sends the ServerGameReconnectionMessage and the ServerStartTurnMessage needed for a saved game to start.
     * @param viewList
     */
    public void loadFromFile(List<RealRemoteViewHandler> viewList){

        ArrayList<User> users = new ArrayList<>();
        ArrayList<Player> players = model.getPlayers();

        for(Player p: players) {
            p.setDisconnected(false);
            users.add(model.getUserFromPlayer(p));
        }

        int blackCross = (model.isSolo()) ? model.getLorenzo().getBlackCross() : 0;

        List<PlayerView> playerViews = new ArrayList<>();
        model.getPlayers().forEach((p)->playerViews.add(p.getVisible()));

        for(RealRemoteViewHandler view : viewList){
            Player p = model.getPlayerFromUser(view.getUser());
            ServerGameReconnectionMessage message = new ServerGameReconnectionMessage(
                    false,
                    playerViews,
                    model.getCurrentPlayer().getVisible(),
                    model.getMarketInstance().getVisible(),
                    model.getDevelopmentCardsMarket().getVisible(),
                    blackCross);


            Optional<Action> gameAction = p.getGameAction();
            if(gameAction.isPresent()){
                gameAction.get().handleReconnection(p,message);
            }

            view.updateFromGame(message);
        }

        Player currentPlayer = model.getCurrentPlayer();
        int currentPlayerIdx = players.indexOf(currentPlayer);
        Player endingPlayer = (currentPlayerIdx == 0) ? players.get(players.size()-1) : players.get(currentPlayerIdx-1);

        model.notify(new ServerStartTurnMessage(
                model.getUserFromPlayer(currentPlayer),
                model.getUserFromPlayer(endingPlayer)
        ));
        setLobbyState(LobbyState.IN_GAME);
    }

    public void saveToFile(ClientSaveGameMessage action, RemoteViewHandler view, User user){
        Player currentPlayer = model.getPlayerFromUser(user);

        if( !(currentPlayer.getTurn())  ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {
            model.notify(new ServerGameSavingMessage());

            Optional<Action> gameAction = currentPlayer.getGameAction();
            if (gameAction.isPresent()) {
                gameAction.get().handleDisconnection(currentPlayer, this, view, user);
            }
            model.notify(new DisconnectionMessage());
            match.saveToFile();
        }
    }

    public void setupAction(ClientSetupActionMessage action, RemoteViewHandler view, User user){
        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) || model.getGameState() != GameState.SETUP_GAME){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            player.setLeaderCards(action.getChosen());
            Map<Id, Resource> idResourceMap = action.getIdResourceMap();

            for(Id id: idResourceMap.keySet()){
                try {
                    player.getWarehouse().get(id.getValue()).addResource(idResourceMap.get(id));
                } catch (InvalidDepotException e) {
                    //this should never happen in the during the setup
                }
            }

            model.notify(new ServerSetupActionMessage(
                    player.getVisibleWarehouse(),
                    player.getLeaderCards(),
                    model.getUserFromPlayer(player)
            ));


            turnController.endSetupTurn();
        }
    }

    public void handleDisconnection(DisconnectionMessage action, RemoteViewHandler view, User user){
        GameState currentGameState = model.getGameState();
        Player currentPlayer = model.getCurrentPlayer();

        if(currentGameState.equals(GameState.SETUP_GAME)){
            handleSetupDisconnection();
            return;
        }

        if(model.getUserFromPlayer(currentPlayer).equals(user)){
            Optional<Action> gameAction = currentPlayer.getGameAction();
            if(gameAction.isPresent()){
                gameAction.get().handleDisconnection(currentPlayer, this, view, user);
            }
            turnController.forceEndTurn(model.getPlayerFromUser(user));
        }

        match.handleDisconnection(user);
    }

    public void handleReconnection(RemoteViewHandler view, User user){

        Player player = model.getPlayerFromUser(user);
        player.setDisconnected(false);

        int blackCross = (model.isSolo()) ? model.getLorenzo().getBlackCross() : 0;


        List<PlayerView> playerViews = new ArrayList<>();
        model.getPlayers().forEach((p)->playerViews.add(p.getVisible()));
        ServerGameReconnectionMessage message = new ServerGameReconnectionMessage(
                false,
                playerViews,
                model.getCurrentPlayer().getVisible(),
                model.getMarketInstance().getVisible(),
                model.getDevelopmentCardsMarket().getVisible(),
                blackCross);


        Optional<Action> gameAction = player.getGameAction();
        gameAction.ifPresent(action -> action.handleReconnection(player, message));

        view.updateFromGame(message);

        if(model.getGameState().equals(GameState.WAITING_FOR_SOMEONE)){
            model.setGameState(GameState.PLAY);
            Player endingPlayer = model.getCurrentPlayer();
            model.setCurrentPlayer(player);
            player.toggleTurn();
            player.toggleMainAction();
            model.notify(new ServerStartTurnMessage(
                    model.getUserFromPlayer(player),
                    model.getUserFromPlayer(endingPlayer)
            ));
        }
    }

    private void handleSetupDisconnection(){
        match.endGameDuringSetup();
    }

    /**
     * When a game ends this method gets called and the lobby returns in the setup phase
     */
    private void handleEndOfGame(){
        match.endGame();
    }

    public void setLobbyState(LobbyState state){
        match.setLobbyState(state);
    }
}
