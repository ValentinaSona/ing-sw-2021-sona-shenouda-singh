package it.polimi.ingsw.client.ui.gui.GUIControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MainMenuController extends AbstractGUIController {

    @FXML
    private TextField nicknameField;

    @FXML
    private Label emptyNickname;

    public void goToMulti(MouseEvent mouseEvent) {
        change("multiplayerScreen.fxml", "style1.css");
    }

    public void joinGame(MouseEvent mouseEvent) {
        change("joinScreen.fxml", "style1.css");
    }

    public void backToMain(MouseEvent mouseEvent) {
        change("mainScreen.fxml", "style1.css");
    }

    public void backToMulti(MouseEvent mouseEvent) {
        change("multiplayerScreen.fxml", "style1.css");
    }

    public void createGame(MouseEvent mouseEvent) {
    }

    public void goToJoin(MouseEvent mouseEvent) {
        if (nicknameField.getText().isEmpty()) emptyNickname.setOpacity(1);
        else emptyNickname.setOpacity(0);
    }
}
