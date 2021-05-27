package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.MatchSettings;
import it.polimi.ingsw.client.ui.UIController;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;


public class LobbyGUIController extends AbstractGUIController {

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
        players.getItems().add(UIController.getInstance().getClientNickname());
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
        numPlayersLabel.setText(UIController.getInstance().currentPlayerNum()
                + "/" + UIController.getInstance().totalPlayerNum()
                + " players");
    }

    // Method exclusively for testing purposes
    public void addPlaceholder(MouseEvent mouseEvent) {
        if (UIController.getInstance().currentPlayerNum() < UIController.getInstance().totalPlayerNum())
            players.getItems().add("Placeholder");

        MatchSettings.getInstance().addPlayer("Placeholder");

        if (UIController.getInstance().currentPlayerNum() == UIController.getInstance().totalPlayerNum()) {
            timeline.stop();
            loading.setOpacity(0);
            starting.setOpacity(1);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setMaximized(true);
            change(ScreenName.LEADER_SELECTION, mouseEvent);

        }

        updatePlayersHeader();
    }
}
