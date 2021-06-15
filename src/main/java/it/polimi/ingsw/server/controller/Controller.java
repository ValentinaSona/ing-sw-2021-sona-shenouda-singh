package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.Match;
import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.server.view.ViewClientMessage;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.DisconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import it.polimi.ingsw.utils.observer.LambdaObserver;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements LambdaObserver {
    private Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static Controller singleton;
    public final DevelopmentCardMarketController developmentCardMarketController;
    public final LeaderCardsController leaderCardsController;
    public final MarketController marketController;
    public final ResourceController resourceController;
    public final TurnController turnController;
    private final Game model;
    private final Match match;
    private final BlockingQueue<ViewClientMessage> actionToProcess = new LinkedBlockingDeque<>();

    public static Controller getInstance(Game model,Match matchInstance){
        if(singleton == null){
            singleton = new Controller(model, matchInstance);
        }

        return singleton;
    }

    private Controller(Game modelInstance, Match matchInstance){
        match = matchInstance;
        model = modelInstance;
        developmentCardMarketController = DevelopmentCardMarketController.getInstance(model);
        leaderCardsController = LeaderCardsController.getInstance(model);
        marketController = MarketController.getInstance(model);
        resourceController = ResourceController.getInstance(model);
        turnController = TurnController.getInstance(model);
    }

    /**
     * This method is called by the views and adds the action to the processing queue
     *
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
            handleable.handleMessage(this, action.view, action.user);



        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
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
                    //TODO: not true, the fourth player receives two different resources. But, since
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

    //TODO
    public void handleDisconnection(DisconnectionMessage action, RemoteViewHandler view, User user){
        match.handleDisconnection(user);
        if(model.getCurrentPlayer().equals(model.getPlayerFromUser(user)){
            turnController.forceEndTurn(model.getCurrentPlayer());
        }

    }

    //TODO
    public void handleReconnection(ClientGameReconnectionMessage action, RemoteViewHandler view, User user){
        match.handleReconnection();

    }

}
