package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import javafx.application.Platform;

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

        currentController.handleStatusMessage(message);

    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {
        Platform.runLater(() -> ((LobbyGUIController)currentController).lobbyUpdate(message));
    }

    public void handleSetupGameMessage(ServerSetupGameMessage message) {
        Platform.runLater(() -> {
            GUIHelper.getInstance().buildNickList(message.getUsers());

            synchronized (DispatcherController.getInstance()) {
                ((LobbyGUIController)currentController).goToGame();
            }
        });

    }

    public void handleSetupActionMessage(ServerSetupActionMessage message) {
        ((LeaderSelectionGUIController)currentController).handleSetupActionMessage(message);
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
            GameLog.getInstance().update(Action.TURN, message.getStartingTurn());
            currentGameController.update();


    }

    public void handleServerWarehouseMessage(ServerWarehouseMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())
                && GUIHelper.getInstance().getCurrentScreen() == ScreenName.PERSONAL_BOARD)
            Platform.runLater(() -> ((BoardGUIController)currentGameController).updateDepot());
    }

    public void handleServerBoughtMarblesMessage(ServerBoughtMarblesMessage message) {
        if (!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            GameLog.getInstance().update(Action.BUY_MARKET, message.getUser(), message.getBoughtResources());
            Platform.runLater(() -> GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD));


    }

    public void handleServerDepositActionMessage(ServerDepositActionMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())
                && GUIHelper.getInstance().getCurrentScreen() == ScreenName.PERSONAL_BOARD)
            Platform.runLater(() -> ((BoardGUIController)currentGameController).updateDepot());
    }
}
