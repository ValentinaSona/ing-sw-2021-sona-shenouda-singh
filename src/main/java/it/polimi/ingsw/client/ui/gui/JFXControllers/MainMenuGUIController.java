package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.UIController;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;

public class MainMenuGUIController extends AbstractGUIController {

    private int numberOfPlayers = 0;
    private String nickname;

    @FXML
    private TextField nicknameField;
    @FXML
    private Label emptyNickname;
    @FXML
    private Label joining;
    @FXML
    private Button joinButton;
    @FXML
    private SVGPath backArrow;
    @FXML
    private Label chooseNick;
    @FXML
    private Label party;
    @FXML
    private StackPane mainPane;

    public void goToMulti(MouseEvent mouseEvent) {
        change(ScreenName.MULTIPLAYER, mouseEvent);
    }

    public void joinGame(MouseEvent mouseEvent) {
        change(ScreenName.JOIN_GAME, mouseEvent);
    }

    public void backToMain(MouseEvent mouseEvent) {
        change(ScreenName.MAIN_MENU, mouseEvent);
    }

    public void backToMulti(MouseEvent mouseEvent) {
        change(ScreenName.MULTIPLAYER, mouseEvent);
    }

    public void goToJoin(ActionEvent actionEvent) {
        if (nicknameField.getText().isEmpty()) emptyNickname.setOpacity(1);
        else {
            emptyNickname.setOpacity(0);
            chooseNick.setOpacity(0);
            nicknameField.setEditable(false);
            backArrow.setDisable(true);

            joining.setOpacity(1);
            joinButton.setOpacity(0);
            joinButton.setDisable(true);

            boolean success = UIController.getInstance().sendNickname(nicknameField.getText());

            if(!success) {
                // TODO mostrare messaggio di errore
                joining.setOpacity(0);
                chooseNick.setOpacity(1);
                joinButton.setOpacity(1);
                joinButton.setDisable(false);
                nicknameField.setEditable(true);
            }
            else {
                nickname = nicknameField.getText();
                if(nickname.equals("a nickname") || nickname.equals("a Nickname") ||
                        nickname.equals("Nickname") || nickname.equals("nickname"))
                    party.setOpacity(1);
                boolean isFirst = UIController.getInstance().joinLobby();
                if (isFirst) setGameCreation(actionEvent);
                else setJoiningGame();
            }
        }
    }

    public void setGameCreation(ActionEvent actionEvent) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(300), nicknameField);
        translate.setInterpolator(Interpolator.EASE_OUT);

        nicknameField.setStyle("-fx-border-color: white;" +
                "-fx-border-width: 0 0 0 0;" +
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #5a9cf2;" +
                "-fx-font-family: \"Crimson Pro Medium\";" +
                "-fx-font-size: 40px;" +
                "-fx-alignment: center;");

        translate.setByY(-120);
        translate.setOnFinished(e -> {
            GameCreationGUIController.setNickname(nickname);
            change(ScreenName.CREATION, actionEvent);
        });
        translate.play();
    }

    public void setJoiningGame() {}

    public void goToJoinEnter(ActionEvent actionEvent) {
        goToJoin(actionEvent);
    }

    public void fullscreenSelected(ActionEvent actionEvent) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    public void goToOptions(MouseEvent mouseEvent) {
        change(ScreenName.OPTIONS, mouseEvent);
    }

    public void goToSingleplayer(MouseEvent mouseEvent) {
        change(ScreenName.SINGLEPLAYER, mouseEvent);
    }

    public void goToCredits(MouseEvent mouseEvent) {
        change(ScreenName.CREDITS, mouseEvent);
    }
}
