package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.MatchSettings;
import it.polimi.ingsw.client.ui.UIController;
import it.polimi.ingsw.client.ui.UiControllerInterface;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameCreationGUIController extends AbstractGUIController implements UiControllerInterface {

    private int numberOfPlayers;
    //non sapervo come trattare il metodo della setCreationGame questa
    //cosa è da sistemare

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
            //chiedo al server di creare una nuova partita
            UIController.getInstance().setCreation(this, numberOfPlayers);
        }

    }

    public void handleSetCountConfirmation(boolean success){
        if(success){
            change(ScreenName.LOBBY);
        }else{
            //messaggio d'errore a video
        }
    }
    @Override
    public void handleMessage(Transmittable message) {
        if(message instanceof StatusMessage){
            StatusMessage status = (StatusMessage) message;
            if(status.equals(StatusMessage.OK_COUNT)){
                //ho ricevuto l'ok dal server
                handleSetCountConfirmation(true);
            }
        }
    }
}
