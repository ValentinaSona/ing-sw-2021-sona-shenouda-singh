package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.cli.controllers.CLIMessageHandler;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import it.polimi.ingsw.utils.observer.LambdaObserver;
import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DispatcherController implements Runnable, LambdaObserver {
    private static DispatcherController singleton;
    private UiControllerInterface currentController;
    private final BlockingQueue<Transmittable> serverMessages= new LinkedBlockingDeque<>();
    private final boolean gui;

    public static DispatcherController getInstance(boolean gui){
        if(singleton == null){
            singleton = new DispatcherController(gui);
        }

        return singleton;
    }

    //method to use only when the class has already been created
    public static DispatcherController getInstance() {
        return singleton;
    }

    private DispatcherController(boolean gui){
        this.gui = gui;
    }

    public void setCurrentController(UiControllerInterface currentController) {
        this.currentController = currentController;
    }

    public UiControllerInterface getCurrentController() {
        return currentController;
    }

    public void update(Transmittable serverMessage){
        try{
            this.serverMessages.put(serverMessage);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (true){
            try{
                Transmittable message = this.serverMessages.take();
                if(message instanceof StatusMessage){
                    this.handleStatus((StatusMessage)message);
                }else{
                    ClientHandleable handleable = (ClientHandleable) message;
                    ((ClientHandleable) message).handleMessage(this);
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    //TODO
    public void handleWarehouse(ServerWarehouseMessage message){}
    //TODO
    public void handleThrowResource(ServerThrowResourceMessage message){}
    //TODO
    public void handleThrowLeaderCard(ServerThrowLeaderCardMessage message){}
    //TODO
    public void handleStartTurn(ServerStartTurnMessage message){}
    //TODO
    public void handleFaithTrackMessage(ServerFaithTrackMessage message){}
    //TODO
    public void handleDepositIntoSlot(ServerDepositIntoSlotMessage message){}
    //TODO
    public void handleDepositAction(ServerDepositActionMessage  message){}
    //TODO
    public void handleChooseWhiteMarbles(ServerChooseWhiteMarblesMessage message){}
    //TODO
    public void handleBuyDevelopmentCard(ServerBuyDevelopmentCardMessage message){}
    //TODO
    public void handleBoughtMarbles(ServerBoughtMarblesMessage message){}
    //TODO
    public void handleActivateProduction(ServerActivateProductionMessage message){}
    //TODO
    public void handleActivateLeaderCardAbility(ServerActivateLeaderCardAbilityMessage message){}
    //TODO
    public void handleSetupGame(ServerSetupGameMessage message){
        GameView.getInstance(message.getUsers());
        GameView.getInstance().setMarketInstance(message.getMarketView());
        GameView.getInstance().setDevelopmentCardsMarket(message.getDevMarketView());

        if(gui){
            ((LobbyMenuController)currentController).handleSetupGameMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerSetupGameMessage(message);
        }
    }
    //TODO
    public void handleUpdateLobby(ServerUpdateLobbyMessage message){
        if(gui){
            ((LobbyMenuController)currentController).handleUpdateLobbyMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleUpdateLobbyMessage(message);
        }
    };
    //TODO
    public void handleSetupAction(ServerSetupActionMessage message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouseView());
        if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
            GameView.getInstance().getPlayerFromUser(message.getUser()).setLeaderCards(message.getChosen());
        }

        if(gui){
            ((LeaderCardSelectionController)currentController).handleSetupActionMessage(message);
        }else {

        }
    }
    //TODO
    public void handleSetupUser(ServerSetupUserMessage message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setFaithTrackView(message.getFaithTrackView());
        if(gui){
            ((LeaderCardSelectionController)currentController).handleSetupUserMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerSetupUserMessage(message);
        }
    }
    //TODO
    public void handleStatus(StatusMessage message){
        if(gui){
            currentController.handleStatusMessage(message);
        }else{
            //come gestisce gli status message la cli
            CLIMessageHandler.getInstance().handleStatusMessage(message);
        }
    }
}
