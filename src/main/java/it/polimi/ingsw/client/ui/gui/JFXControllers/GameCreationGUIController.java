package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.UIController;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GameCreationGUIController extends AbstractGUIController {
    private static String nickname;

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
    private ListView<String> players;

    @FXML
    private void initialize() {
        numberOfPlayers = 0;
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
        numberOfPlayers = Integer.parseInt(((Button)actionEvent.getSource()).getText());
    }

    public void create(ActionEvent actionEvent) {
        if (numberOfPlayers == 0) {
            selectNumLabel.setOpacity(0);
            noNumSelected.setOpacity(1);
        }

        else{
            UIController.getInstance().setCreation(numberOfPlayers);
            change(ScreenName.LOBBY, actionEvent);
            //players.getItems().add(nickname);
        }

    }
}
