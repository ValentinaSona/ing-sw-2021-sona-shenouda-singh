package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.UIController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.SVGPath;

public class MainMenuGUIController extends AbstractGUIController {

    private int numberOfPlayers = 0;

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
    private AnchorPane creationPane;
    @FXML
    private Button twoPlayers;
    @FXML
    private Button threePlayers;
    @FXML
    private Button fourPlayers;

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

    public void goToJoin(MouseEvent mouseEvent) {
        if (nicknameField.getText().isEmpty()) emptyNickname.setOpacity(1);
        else {
            emptyNickname.setOpacity(0);
            nicknameField.setEditable(false);
            backArrow.setDisable(true);

            joining.setOpacity(1);
            joinButton.setOpacity(0);
            joinButton.setDisable(true);

            boolean success = UIController.getInstance().sendNickname(nicknameField.getText());

            if(!success) {
                // TODO mostrare messaggio di errore
                joining.setOpacity(0);
                joinButton.setOpacity(1);
                joinButton.setDisable(false);
            }
            else {
                boolean isFirst = UIController.getInstance().joinLobby();
                if (isFirst) setGameCreation();
                else setJoiningGame();
            }
        }
    }

    public void setGameCreation() {
        creationPane.setOpacity(1);
        creationPane.setDisable(false);
        joining.setOpacity(0);
    }

    public void setJoiningGame() {}

    public void playersSelected(MouseEvent mouseEvent) {
        Button pressed = (Button)mouseEvent.getSource();
        pressed.setStyle("-fx-border-color: white; -fx-border-width: 5px;");
        pressed.setTranslateX(-5);
        pressed.setTranslateY(-5);

        int selected = Integer.parseInt(pressed.getText());

        if (selected != numberOfPlayers) {

            switch (numberOfPlayers) {
                case 2 -> deactivate(twoPlayers);
                case 3 -> deactivate(threePlayers);
                case 4 -> deactivate(fourPlayers);
            }

            numberOfPlayers = selected;

        }
    }

    public void deactivate(Button button) {
        button.setStyle("");
        button.setTranslateX(0);
        button.setTranslateY(0);
    }

    public void createGame(MouseEvent mouseEvent) {
    }
}
