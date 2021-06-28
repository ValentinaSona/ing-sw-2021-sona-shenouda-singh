package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLIMessageHandler;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionGameSetupMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

public class GUIMessageHandler {

    private static GUIMessageHandler singleton;
    private UiControllerInterface currentController;
    private GameGUIControllerInterface currentGameController;

    public void setCurrentController(UiControllerInterface currentController) {
        GUIHelper.getInstance().setCurrentController(currentController);
        this.currentController = currentController;
    }

    public void setCurrentGameController(GameGUIControllerInterface controller) {
        GUIHelper.getInstance().setCurrentGameController(controller);
        this.currentGameController = controller;
    }

    public static GUIMessageHandler getInstance() {
        if(singleton==null) singleton = new GUIMessageHandler();
        return singleton;
    }

    public void handleStatusMessage(StatusMessage message) {

        System.out.println(message);

        Platform.runLater(() -> {
            switch(GUIHelper.getInstance().getCurrAction()){
                case SELECTED_SLOT -> ((BoardGUIController)currentController).startDevResSelection();
                case CHOOSING_NICK -> {
                    if (message == StatusMessage.CLIENT_ERROR) ((MainMenuGUIController)currentController).sameNick();
                }
            }
        });
        currentController.handleStatusMessage(message);

    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {
        Platform.runLater(() -> ((LobbyGUIController)currentController).lobbyUpdate(message));
    }

    public void handleSetupGameMessage(ServerSetupGameMessage message) {
        Platform.runLater(() -> {
            GUIHelper.getInstance().buildNickList(message.getUsers());

            if (GUIHelper.getInstance().isLocal()) ((MainMenuGUIController)currentController).goToGame();

            else{
                synchronized (DispatcherController.getInstance()) {
                    ((LobbyGUIController)currentController).goToGame();
                }
            }
        });

    }

    public void handleSetupActionMessage(ServerSetupActionMessage message) {

        synchronized (getInstance()) {

            while(GUIHelper.getInstance().getCurrentScreen() != ScreenName.STARTING_CHOICE) {
                try {
                    getInstance().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ((LeaderSelectionGUIController)currentController).handleSetupActionMessage(message);
        }
    }

    public void handleServerSetupUserMessage(ServerSetupUserMessage message) {
        ((LeaderSelectionGUIController)currentController).handleSetupUserMessage(message);
    }

    public void handleServerStartTurnMessage(ServerStartTurnMessage message) {

            // Game is in setup phase
            if (GUIHelper.getInstance().getCurrentScreen() == ScreenName.STARTING_CHOICE) Platform.runLater( () -> ((LeaderSelectionGUIController)currentController).goToGame());

            synchronized (GUIMessageHandler.getInstance()) {
                while(currentGameController == null) {
                    try {
                        GUIMessageHandler.getInstance().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Game has begun
            GUIHelper.getInstance().setTurn(message.getStartingTurn().getNickName().equals(MatchSettings.getInstance().getClientNickname()));
            GameLog.getInstance().update(LogUpdates.TURN, message.getStartingTurn());
            currentGameController.update();


    }

    public void handleServerWarehouseMessage(ServerWarehouseMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())
                && GUIHelper.getInstance().getCurrentScreen() == ScreenName.PERSONAL_BOARD)
            Platform.runLater(() -> ((BoardGUIController)currentGameController).updateDepot());
    }

    public void handleServerBoughtMarblesMessage(ServerBoughtMarblesMessage message) {
        if (!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            GameLog.getInstance().update(LogUpdates.BUY_MARKET, message.getUser(), message.getBoughtResources());
            Platform.runLater(() -> GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD));


    }

    public void handleServerDepositActionMessage(ServerDepositActionMessage message) {

        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())
                && GUIHelper.getInstance().getCurrentScreen() == ScreenName.PERSONAL_BOARD) {

            if (GUIHelper.getInstance().getClientView().getTempResources().size() == 0) {
                Platform.runLater(() -> GUIHelper.getInstance().getCurrentGameController().update());
            }
            Platform.runLater(() -> ((BoardGUIController)currentGameController).updateDepot());
        }

    }

    public void handleServerBuyDevelopmentCardMessage(ServerBuyDevelopmentCardMessage message) {
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerDepositIntoSlotMessage(ServerDepositIntoSlotMessage message) {
        GUIHelper.getInstance().setChosenCard(false);
        if (GUIHelper.getInstance().getCurrAction() == CurrAction.DEV_CONFIRMATION )
            UIController.getInstance().buyTargetCard(GUIHelper.getInstance().getSelectedSlot());
        else SelectedProductions.getInstance().confirm();
        GUIHelper.getInstance().setCurrAction(CurrAction.IDLE);
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerActivateLeaderCardAbilityMessage(ServerActivateLeaderCardAbilityMessage message) {
        GUIHelper.getInstance().abilityActivation(message.getAbility());
        Platform.runLater(() -> currentGameController.update());
        GameLog.getInstance().update(LogUpdates.ABILITY_ACTIVATION, message.getUser());
    }

    public void handleServerThrowLeaderCardAbilityMessage(ServerThrowLeaderCardMessage message) {
        Platform.runLater(() -> currentGameController.update());
        GameLog.getInstance().update(LogUpdates.CARD_THROW, message.getUser());
    }

    public void handleServerActivateProductionMessage(ServerActivateProductionMessage message) {
        SelectedProductions.getInstance().reset();
        GameLog.getInstance().update(LogUpdates.PRODUCTION_ACTIVATED, message);
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerFinalScoreMessage(ServerFinalScoreMessage message) {
        GUIHelper.getInstance().setScreen(ScreenName.END_OF_GAME);
    }

    public void handleServerServerSoloDiscardMessage(ServerSoloDiscardMessage message) {
        GameLog.getInstance().update(LogUpdates.SOLO_DISCARD, message);
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerSoloMoveMessage(ServerSoloMoveMessage message) {
        GameLog.getInstance().update(LogUpdates.BLACK_CROSS, message);
        Platform.runLater(() -> currentGameController.update());

    }

    public void handleServerThrowResourceMessage(ServerThrowResourceMessage message) {
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerFaithTrackMessage(ServerFaithTrackMessage message) {
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleDisconnectionGameSetupMessage() {
        Platform.runLater(() -> {
            GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
            GUIHelper.getInstance().setScreen(ScreenName.WARNING);
        });
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GUIHelper.getInstance().setScreen(ScreenName.MAIN_MENU);

    }

    public void handleServerGameReconnectionMessage(ServerGameReconnectionMessage message) {
        GameLog.getInstance().update(LogUpdates.RECONNECTION);
    }
}
