package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.Constant;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;

import java.io.IOException;

public class JoinSingleplayerGUIController extends AbstractGUIController{

    @FXML
    private Label emptyNickname, chooseNick, joining;
    @FXML
    private Button joinButton;
    @FXML
    private TextField nicknameField;
    @FXML
    private SVGPath backArrow;

    public void goToJoinEnter(ActionEvent actionEvent) {
        startGame(actionEvent);
    }

    public void startGame(ActionEvent actionEvent) {
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
                    UIController.getInstance().sendNickname(nicknameField.getText(), Constant.hostIp(), Constant.port());
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

    public void backToSingle(MouseEvent mouseEvent) {
        change(ScreenName.SINGLEPLAYER);
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
