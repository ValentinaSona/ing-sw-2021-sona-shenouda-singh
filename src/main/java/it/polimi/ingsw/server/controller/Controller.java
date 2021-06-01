package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.server.view.ViewClientMessage;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import it.polimi.ingsw.utils.observer.LambdaObserver;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Controller implements LambdaObserver {
    private static Controller singleton;
    public final DevelopmentCardMarketController developmentCardMarketController;
    public final LeaderCardsController leaderCardsController;
    public final MarketController marketController;
    public final ResourceController resourceController;
    public final TurnController turnController;
    private final Game model;
    private final BlockingQueue<ViewClientMessage> actionToProcess = new LinkedBlockingDeque<>();

    public static Controller getInstance(Game model){
        if(singleton == null){
            singleton = new Controller(model);
        }

        return singleton;
    }

    private Controller(Game modelInstance){
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

    //TODO fase di setup in cui vengono mandati ai player la richiesta della scelta delle carte
    public void setup(){
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Player> players = model.getPlayers();

        for(Player p: players) {
            users.add(model.getUserFromPlayer(p));
        }

        model.notify(new ServerSetupGameMessage(
                users
        ));

        LeaderCardsKeeper keeper = model.getLeaderCardsKeeper();
        //ad ogni player invio le leaderCard da cui deve scegliere e
        //le risorse iniziali che deve scegliere e di quanto è avanzato sul tracciato fede
        //TODO: scriver meglio questo ciclo for ero di fretta e non avevo altre idee
        for(int i = 0; i < players.size(); i++){
            switch (i){
                case 0:
                    model.notify(new ServerSetupUserMessage(
                            0,
                            players.get(i).getVisibleFaithTrack(),
                            keeper.pickFour(),
                            model.getUserFromPlayer(players.get(i))
                    ));
                    break;
                case 1:
                    model.notify(new ServerSetupUserMessage(
                            1,
                            players.get(i).getVisibleFaithTrack(),
                            keeper.pickFour(),
                            model.getUserFromPlayer(players.get(i))
                    ));
                    break;
                case 2:
                    try {
                        players.get(i).getFaithTrack().addFaithPoints(1);
                    } catch (VaticanReportException e) {
                        //during the setup this exception will never be thrown
                        e.printStackTrace();
                    }
                    model.notify(new ServerSetupUserMessage(
                            1,
                            players.get(i).getVisibleFaithTrack(),
                            keeper.pickFour(),
                            model.getUserFromPlayer(players.get(i))
                    ));
                    break;
                case 3:
                    try {
                        players.get(i).getFaithTrack().addFaithPoints(1);
                    } catch (VaticanReportException e) {
                        //during the setup this exception will never be thrown
                        e.printStackTrace();
                    }
                    model.notify(new ServerSetupUserMessage(
                            2,
                            players.get(i).getVisibleFaithTrack(),
                            keeper.pickFour(),
                            model.getUserFromPlayer(players.get(i))
                    ));
                    break;
            }
        }

        //adesso il primo giocatore della lista user avrà la view utilizzabile e quindi potrà
        //scegliere le risorse da usare e le leaderCard scelte
        model.setCurrentPlayer(players.get(0));
        Player startingPlayer = model.getCurrentPlayer();
        startingPlayer.toggleTurn();
        startingPlayer.toggleMainAction();

        model.notify(new ServerStartSetupTurnMessage(
                model.getUserFromPlayer(startingPlayer),
                model.getMarketInstance().getVisible(),
                model.getDevelopmentCardsMarket().getVisible()
        ));
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
                    (LeaderCard[]) player.getLeaderCards().toArray(),
                    model.getUserFromPlayer(player)
            ));

            ArrayList<Player> players = model.getPlayers();

            if(players.indexOf(player) == players.size()-1){
                model.setGameState(GameState.PLAY);
            }
            ClientEndTurnMessage endTurnMessage =new ClientEndTurnMessage();
            turnController.endTurn(endTurnMessage, view, user);
        }
    }
}
