package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Singleton class containing the GUI-specific methods for handling the server messages.
 * They are invoked by the DispatcherController after the UI-agnostic handling of the messages.
 */
public class GUIMessageHandler {

    private static GUIMessageHandler singleton;
    private UiControllerInterface currentController;
    private GameGUIControllerInterface currentGameController;

    final GameLog log;

    public void setCurrentController(UiControllerInterface currentController) {
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

    private GUIMessageHandler() {
        log = GameLog.getInstance();
    }

    public void handleStatusMessage(StatusMessage message) {

        Platform.runLater(() -> {
            switch(GUIHelper.getInstance().getCurrAction()){
                case SELECTING_SLOT -> log.update(LogUpdates.DEV_SLOT);
                case SELECTED_SLOT -> {
                    ((BoardGUIController)currentController).startDevResSelection();
                    log.update(LogUpdates.DEV_RES);
                }
                case DEV_CONFIRMATION -> {
                    if(message == StatusMessage.SELECTION_ERROR) log.update(LogUpdates.DEV_RES_ERROR);
                }
                case CHOOSING_NICK -> {
                    if (message == StatusMessage.CLIENT_ERROR) ((MainMenuGUIController)currentController).sameNick();
                }
            }
        });
        currentController.handleStatusMessage(message);

    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {
        if (GUIHelper.getInstance().isResuming()) Platform.runLater(() -> GUIHelper.getInstance().setScreen(ScreenName.LOBBY));
        synchronized (GUIMessageHandler.getInstance()){
            while (GUIHelper.getInstance().getCurrentScreen() != ScreenName.LOBBY) {
                try {
                    GUIMessageHandler.getInstance().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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

        GUIHelper.getInstance().setCurrAction(CurrAction.IDLE);

        // Game is in setup phase
        if (GUIHelper.getInstance().getCurrentScreen() == ScreenName.STARTING_CHOICE) Platform.runLater( () -> ((LeaderSelectionGUIController)currentController).goToGame());

        else if (GUIHelper.getInstance().getCurrentScreen() == ScreenName.LOBBY && GUIHelper.getInstance().isResuming()) Platform.runLater(() -> GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD));

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
        log.update(LogUpdates.TURN, message.getStartingTurn());
        currentGameController.update();


    }

    public void handleServerWarehouseMessage(ServerWarehouseMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())
                && GUIHelper.getInstance().getCurrentScreen() == ScreenName.PERSONAL_BOARD)
            Platform.runLater(() -> ((BoardGUIController)currentGameController).updateDepot());
    }

    public void handleServerBoughtMarblesMessage(ServerBoughtMarblesMessage message) {
        log.update(LogUpdates.BUY_MARKET, message.getUser(), message.getBoughtResources());
        Platform.runLater(() -> {
            if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD);

            else currentGameController.update();
        });

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
        log.update(LogUpdates.BUY_DEV, message);
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
        Platform.runLater(() -> currentGameController.update());
        log.update(LogUpdates.ABILITY_ACTIVATION, message.getUser());
    }

    public void handleServerThrowLeaderCardAbilityMessage(ServerThrowLeaderCardMessage message) {
        Platform.runLater(() -> currentGameController.update());
        log.update(LogUpdates.CARD_THROW, message.getUser());
    }

    public void handleServerActivateProductionMessage(ServerActivateProductionMessage message) {
        SelectedProductions.getInstance().reset();
        log.update(LogUpdates.PRODUCTION_ACTIVATED, message);
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleServerFinalScoreMessage(ServerFinalScoreMessage message) {
        Platform.runLater(() -> GUIHelper.getInstance().setScreen(ScreenName.END_OF_GAME));
        GUIHelper.getInstance().setFinalScore(message);
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
        log.update(LogUpdates.FAITH_TRACK, message);
        Platform.runLater(() -> currentGameController.update());
    }

    public void handleDisconnectionGameSetupMessage() {
        Platform.runLater(() -> {
            GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
            GUIHelper.getInstance().setScreen(ScreenName.WARNING);
        });

    }

    public void handleServerGameReconnectionMessage(ServerGameReconnectionMessage message) {
        GUIHelper.getInstance().restoreNickList(message.getPlayerViews());
        if (message.isPendingAction()) GUIHelper.getInstance().setChoosingTemp(true);
        for (var p : message.getPlayerViews()) {
            if (p.getNickname().equals(MatchSettings.getInstance().getClientNickname())) {
                GUIHelper.getInstance().setClientView(p);
            }
            if (GUIHelper.getInstance().isReconnecting()) {
                Platform.runLater(() -> {
                    var stage = (Stage) GUIHelper.getInstance().getCurrentScene().getWindow();
                    if (GUIHelper.getInstance().getResolution() > 1080) {
                        stage.setWidth(1920);
                        stage.setHeight(1080);
                        GUIHelper.getInstance().centerScreen(stage);
                    }
                    else {
                        stage.setMaximized(true);
                    }
                    GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD);
                });
            }
        }
    }

    public void handleServerChooseWhiteMarblesMessage(ServerChooseWhiteMarblesMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            log.update(LogUpdates.CHOOSE_WHITE);
            Platform.runLater(() -> ((MarketGUIController) currentController).startTwoCardSelection());
        }
    }

    public void handleGameSaving() {
        GUIHelper.getInstance().setCurrAction(CurrAction.SAVING_GAME);
        Platform.runLater(() -> {
            GUIHelper.getInstance().setScreenshot(GUIHelper.getInstance().getCurrentScene().snapshot(null));
            GUIHelper.getInstance().setScreen(ScreenName.WARNING);
        });
    }
    // TODO handle classcastexception
    public void handleServerDisconnectionMessage() {
        if (GUIHelper.getInstance().getCurrentScreen() == ScreenName.JOIN_GAME || GUIHelper.getInstance().getCurrentScreen() == ScreenName.JOIN_SINGLEPLAYER)
            Platform.runLater(() -> ((MainMenuGUIController)currentController).noGameFound());
    }

    public void handleLastTurns(ServerLastTurnsMessage message) {
        log.update(LogUpdates.END, message);
    }
}
