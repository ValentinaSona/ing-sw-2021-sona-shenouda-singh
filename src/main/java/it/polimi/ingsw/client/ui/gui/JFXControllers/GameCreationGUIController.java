package it.polimi.ingsw.client.ui.gui.JFXControllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameCreationGUIController extends AbstractGUIController {
    private static String nickname;

    @FXML
    private Label nicknameLabel;
    @FXML
    private StackPane creationPanel;

    @FXML
    private void initialize() {
        nicknameLabel.setText(nickname);
        FadeTransition fade = new FadeTransition(Duration.millis(300), creationPanel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public static void setNickname(String nickname) {
        GameCreationGUIController.nickname = nickname;
    }


    public void numSelected(ActionEvent actionEvent) {
    }
}
