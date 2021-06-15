package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;
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

    public void handleStartTurn() {
        ((LeaderSelectionGUIController)currentController).goToGame();
    }
}
