package it.polimi.ingsw.client.ui.gui.JFXControllers;


import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.LobbyMenuController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.server.controller.User;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class LobbyGUIController extends AbstractGUIController implements UiControllerInterface {

    Timeline timeline;

    @FXML
    private ListView<String> players;
    @FXML
    private Label numPlayersLabel;
    @FXML
    private ImageView loading;
    @FXML
    private Label starting;
    @FXML
    private StackPane mainPane;

    @FXML
    private void initialize() {

        GUIHelper.getInstance().setCurrentScreen(ScreenName.LOBBY);

        players.getItems().add(MatchSettings.getInstance().getClientNickname());
        updatePlayersHeader();

        starting.setOpacity(0);

        timeline = GUIHelper.loadingWheel(loading);

        GUIHelper.getInstance().setSetUpDone(false);
    }

    private void updatePlayersHeader() {
        numPlayersLabel.setText(MatchSettings.getInstance().getCurrentUsersNum()
                + "/" + MatchSettings.getInstance().getTotalUsers()
                + " players");
    }

    public void lobbyUpdate (ServerUpdateLobbyMessage message) {
        List<User> lobbyUsers = message.getLobbyUsers();
        MatchSettings.getInstance().setTotalUsers(message.getNumOfPlayer());

        players.getItems().clear();
        MatchSettings.getInstance().setJoiningUsers(lobbyUsers);

        for(User u : lobbyUsers){
            players.getItems().add(u.getNickName());
        }

        updatePlayersHeader();

        if (MatchSettings.getInstance().getCurrentUsersNum() == MatchSettings.getInstance().getTotalUsers()) {
            timeline.stop();
            loading.setOpacity(0);
            starting.setOpacity(1);

            Stage stage = (Stage) mainPane.getScene().getWindow();

            if (GUIHelper.getInstance().getResolution() > 1080) {
                stage.setWidth(1920);
                stage.setHeight(1080);
            }

            else stage.setMaximized(true);

        }
    }

    public void goToGame(){
        change(ScreenName.STARTING_CHOICE);
        DispatcherController.getInstance().notifyAll();
    }

    //TODO
    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
