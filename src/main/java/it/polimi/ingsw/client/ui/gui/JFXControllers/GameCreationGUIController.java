package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Handles the game creation screen
 */
public class GameCreationGUIController extends AbstractGUIController implements UiControllerInterface {

    private int numberOfPlayers;

    @FXML
    private Label nicknameLabel;
    @FXML
    private StackPane creationPanel;
    @FXML
    private Label selectNumLabel;
    @FXML
    private Label noNumSelected;

    @FXML
    private void initialize() {
        GUIHelper.getInstance().setCurrentScreen(ScreenName.CREATION);
        numberOfPlayers = 0;
        nicknameLabel.setText(MatchSettings.getInstance().getClientNickname());
        FadeTransition fade = new FadeTransition(Duration.millis(300), creationPanel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    public void numSelected(ActionEvent actionEvent) {
        numberOfPlayers = Integer.parseInt(((Button)actionEvent.getSource()).getText());
    }

    public void create(ActionEvent actionEvent) {
        if (numberOfPlayers == 0) {
            selectNumLabel.setOpacity(0);
            noNumSelected.setOpacity(1);
        }

        else{
            UIController.getInstance().setCreation(numberOfPlayers);
        }

    }

    public void handleSetCountConfirmation(boolean success){
        Platform.runLater(() -> {
            if(success){
                change(ScreenName.LOBBY);
            }else{
                selectNumLabel.setText("An error occurred");
            }
        });
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

        Platform.runLater(() -> {
            if(message.equals(StatusMessage.OK_COUNT)){
                handleSetCountConfirmation(true);
            }
        });

    }
}
