package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.ViewClientMessage;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.observer.LambdaObserver;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Controller implements LambdaObserver {
    private static Controller singleton;
    public final DevelopmentCardMarketController developmentCardMarketController;
    public final LeaderCardsController leaderCardsController;
    public final MarketController marketController;
    public final ResourceController resourceController;
    public final TurnController turnController;
    private final Model model;
    private final BlockingQueue<ViewClientMessage> actionToProcess = new LinkedBlockingDeque<>();

    public static Controller getInstance(Model model){
        if(singleton == null){
            singleton = new Controller(model);
        }

        return singleton;
    }

    private Controller(Model modelInstance){
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
}
