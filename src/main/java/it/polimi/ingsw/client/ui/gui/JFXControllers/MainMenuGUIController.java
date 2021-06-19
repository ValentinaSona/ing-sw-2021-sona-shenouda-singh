package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainMenuGUIController extends AbstractGUIController {

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
        change(ScreenName.MULTIPLAYER);
    }

    public void joinGame(MouseEvent mouseEvent) {
        change(ScreenName.JOIN_GAME);
    }

    public void backToMain(MouseEvent mouseEvent) {
        change(ScreenName.MAIN_MENU);
    }

    public void backToMulti(MouseEvent mouseEvent) {
        change(ScreenName.MULTIPLAYER);
    }

    @FXML
    public void initialize() {
        GUIHelper.getInstance().setCurrentScreen(ScreenName.MAIN_MENU);
    }

    public void goToJoin(ActionEvent actionEvent) {
        if (nicknameField.getText().isEmpty()) emptyNickname.setOpacity(1);
        else {
            if (nicknameField.getText().length() > 20) {
                chooseNick.setText("Nickname cannot be longer than 20 characters!");
            }
            else {
                emptyNickname.setOpacity(0);
                chooseNick.setOpacity(0);
                nicknameField.setEditable(false);
                backArrow.setDisable(true);

                joining.setOpacity(1);
                joinButton.setOpacity(0);
                joinButton.setDisable(true);

                try {
                    UIController.getInstance().sendNickname(nicknameField.getText(),"127.0.0.1", 10002);
                } catch (IOException e) {
                    chooseNick.setText("Failed to connect...");
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleNicknameConfirmation(boolean success){
        System.out.println("ho ricevuto il messagio");
        if(!success) {
            // TODO mostrare messaggio di errore
            joining.setOpacity(0);
            chooseNick.setOpacity(1);
            joinButton.setOpacity(1);
            joinButton.setDisable(false);
            nicknameField.setEditable(true);
        }
        else {
            String nickname = nicknameField.getText();
            MatchSettings.getInstance().setClientNickname(nicknameField.getText());
            UIController.getInstance().joinLobby();

        }

    }

    public void handleJoinLobbyConfirmation(boolean isFirst){
        if (isFirst) {
            setGameCreation();}
        else
            setJoiningGame();
    }

    public void setGameCreation() {
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
            change(ScreenName.CREATION);
        });
        translate.play();
    }

    public void setJoiningGame() {
        change(ScreenName.LOBBY);
    }

    public void goToJoinEnter(ActionEvent actionEvent) {
        goToJoin(actionEvent);
    }

    public void fullscreenSelected(ActionEvent actionEvent) {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    public void goToOptions(MouseEvent mouseEvent) {
        change(ScreenName.OPTIONS);
    }

    public void goToSingleplayer(MouseEvent mouseEvent) {
        change(ScreenName.SINGLEPLAYER);
    }

    public void goToCredits(MouseEvent mouseEvent) {
        change(ScreenName.CREDITS);
    }

    public void handleStatusMessage(StatusMessage message) {
        Platform.runLater(() -> {
            if(message.equals(StatusMessage.OK_NICK)){
                //posso validare il nickname
                handleNicknameConfirmation(true);
            }else if(message.equals(StatusMessage.SET_COUNT)){
                //posso settare il numero di player
                handleJoinLobbyConfirmation(true);
            }else if(message.equals(StatusMessage.JOIN_LOBBY)){
                //posso settare il numero di player
                handleJoinLobbyConfirmation(false);
            }else if(message.equals(StatusMessage.CLIENT_ERROR)){
                handleNicknameConfirmation(false);
            }
        });
    }
}
