package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.MatchSettings;
import it.polimi.ingsw.client.ui.UIController;
import it.polimi.ingsw.client.ui.UiControllerInterface;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

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
        UIController.getInstance().setCurrentController(this);
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

    private void handleUpdateLobbyUser(ArrayList<User> lobbyUsers) {
        players.getItems().clear();
        MatchSettings.getInstance().setJoiningUsers(lobbyUsers);

        for(User u : lobbyUsers){
            players.getItems().add(u.getNickName());
        }

        updatePlayersHeader();
    }

    private void handleStartGameConfirmation(ArrayList<User> user){
        timeline.stop();
        loading.setOpacity(0);
        starting.setOpacity(1);
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setMaximized(true);
        change(ScreenName.LEADER_SELECTION);
    }

    @Override
    public void handleMessage(Transmittable message) {
        if(message instanceof ServerSetupGameMessage){
            ServerSetupGameMessage  serverMessage = (ServerSetupGameMessage) message;
            handleStartGameConfirmation(serverMessage.getUsers());
        }else if(message instanceof ServerUpdateLobbyMessage){
            ServerUpdateLobbyMessage serverMessage = (ServerUpdateLobbyMessage) message;
            handleUpdateLobbyUser(serverMessage.getLobbyUsers());
        }
    }


}
