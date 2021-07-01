package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.CurrAction;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.utils.Constant;
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
        GUIHelper.getInstance().setLocal(false);
        GUIHelper.getInstance().setSolo(false);
        change(ScreenName.MULTIPLAYER);
    }

    public void joinGame(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setResuming(false);
        GUIHelper.getInstance().setReconnecting(false);
        change(ScreenName.JOIN_GAME);
    }

    public void backToMain(MouseEvent mouseEvent) {
        change(ScreenName.MAIN_MENU);
    }

    public void backToMulti(MouseEvent mouseEvent) {
        if(!GUIHelper.getInstance().isSolo()) change(ScreenName.MULTIPLAYER);
        else change(ScreenName.SINGLEPLAYER);
    }

    @FXML
    public void initialize() {
        GUIHelper.getInstance().setCurrentScreen(ScreenName.MAIN_MENU);
        GUIHelper.getInstance().setCurrAction(CurrAction.CHOOSING_NICK);

        if(chooseNick != null && (GUIHelper.getInstance().isReconnecting() || GUIHelper.getInstance().isResuming()))
            chooseNick.setText("Insert the nickname you had before");
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
                    if (GUIHelper.getInstance().isLocal()) UIController.getInstance().startLocalSinglePlayerGame(nicknameField.getText());
                    else if (GUIHelper.getInstance().isReconnecting()) UIController.getInstance().reconnectToServer(nicknameField.getText(), Constant.hostIp(), Constant.port());
                    else UIController.getInstance().sendNickname(nicknameField.getText(), Constant.hostIp(), Constant.port());
                } catch (IOException e) {
                    chooseNick.setText("Failed to connect...");
                    chooseNick.setOpacity(1);
                    nicknameField.setEditable(true);
                    backArrow.setDisable(false);

                    joining.setOpacity(0);
                    joinButton.setOpacity(1);
                    joinButton.setDisable(false);
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleNicknameConfirmation(boolean success){
        if(!success) {
            joining.setOpacity(0);
            chooseNick.setOpacity(1);
            joinButton.setOpacity(1);
            joinButton.setDisable(false);
            nicknameField.setEditable(true);
            backArrow.setDisable(false);
        }
        else {
            MatchSettings.getInstance().setClientNickname(nicknameField.getText());
            UIController.getInstance().joinLobby();
        }

    }

    public void handleJoinLobbyConfirmation(boolean isFirst){
        if (isFirst) {
            setGameCreation();}
        else
            if (GUIHelper.getInstance().isSolo()) chooseNick.setText("There are already players in the lobby!");
            else setJoiningGame();
    }

    public void setGameCreation() {
        if (!GUIHelper.getInstance().isSolo()) {
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

        else {
            chooseNick.setText("Joining game...");
            UIController.getInstance().setCreation(1);
        }
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
        GUIHelper.getInstance().setSolo(true);
        change(ScreenName.SINGLEPLAYER);
    }

    public void goToCredits(MouseEvent mouseEvent) {
        change(ScreenName.CREDITS);
    }

    public void handleStatusMessage(StatusMessage message) {
        Platform.runLater(() -> {
            if(message.equals(StatusMessage.OK_NICK)){
                //posso validare il nickname
                if(!GUIHelper.getInstance().isResuming()) handleNicknameConfirmation(true);
                else UIController.getInstance().resumeGameFromFile();
            }else if(message.equals(StatusMessage.SET_COUNT)){
                //posso settare il numero di player
                if(!GUIHelper.getInstance().isResuming()) handleJoinLobbyConfirmation(true);
                else UIController.getInstance().loadGameFromFile();
            }else if(message.equals(StatusMessage.JOIN_LOBBY)){
                //posso settare il numero di player
                handleJoinLobbyConfirmation(false);
            }else if(message.equals(StatusMessage.CLIENT_ERROR)){
                handleNicknameConfirmation(false);
            }else if (message.equals(StatusMessage.OK_COUNT)) {
                if (GUIHelper.getInstance().isSolo() || GUIHelper.getInstance().isResuming()) change(ScreenName.LOBBY);
                else chooseNick.setText("An error occurred");
            }
        });
    }

    public void quitGame(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void goToOnlineSingleplayer(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setLocal(false);
        GUIHelper.getInstance().setSolo(true);
        GUIHelper.getInstance().setResuming(false);
        GUIHelper.getInstance().setReconnecting(false);
        change(ScreenName.JOIN_SINGLEPLAYER);
    }

    public void goToLocalSingleplayer(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setLocal(true);
        GUIHelper.getInstance().setSolo(true);
        GUIHelper.getInstance().setResuming(false);
        GUIHelper.getInstance().setReconnecting(false);
        change(ScreenName.JOIN_SINGLEPLAYER);
    }

    public void backToSingle(MouseEvent mouseEvent) {
        change(ScreenName.SINGLEPLAYER);
    }

    public void startGame(ActionEvent actionEvent) {
        goToJoin(actionEvent);
    }

    public void goToGame() {
        if (GUIHelper.getInstance().isLocal()) {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            if (GUIHelper.getInstance().getResolution() > 1080) {
                stage.setWidth(1920);
                stage.setHeight(1080);
            }

            else stage.setMaximized(true);

            change(ScreenName.STARTING_CHOICE);
        }
    }

    public void sameNick() {
        chooseNick.setText("This nickname is already taken!");
    }

    public void resume(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setResuming(true);
        GUIHelper.getInstance().setReconnecting(false);
        change(ScreenName.JOIN_GAME);
    }

    public void resumeSingle(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setResuming(true);
        GUIHelper.getInstance().setReconnecting(false);
        change(ScreenName.JOIN_GAME);
    }

    public void reconnect(MouseEvent mouseEvent) {
        GUIHelper.getInstance().setReconnecting(true);
        change(ScreenName.JOIN_GAME);
    }

    public void noGameFound() {
        chooseNick.setText("No game found");
        handleNicknameConfirmation(false);
    }
}
