package it.polimi.ingsw.client.ui.gui.JFXControllers;


import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.LobbyMenuController;
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
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class LobbyGUIController extends AbstractGUIController implements LobbyMenuController {

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
        players.getItems().add(MatchSettings.getInstance().getClientNickname());
        updatePlayersHeader();

        starting.setOpacity(0);
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), ev -> {
            RotateTransition rotate = new RotateTransition();
            rotate.setAxis(Rotate.Z_AXIS);
            rotate.setByAngle(360);
            rotate.setCycleCount(1);
            rotate.setDuration(Duration.millis(1000));
            rotate.setInterpolator(Interpolator.LINEAR);
            rotate.setNode(loading);
            rotate.play();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void updatePlayersHeader() {
        numPlayersLabel.setText(MatchSettings.getInstance().getCurrentUsersNum()
                + "/" + MatchSettings.getInstance().getTotalUsers()
                + " players");
    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {
        Platform.runLater(() -> {
            List<User> lobbyUsers = message.getLobbyUsers();

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
                stage.setMaximized(true);
            }
        });
    }

    public void handleSetupGameMessage(ServerSetupGameMessage message){
        change(ScreenName.LEADER_SELECTION);
    }

    //TODO
    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
