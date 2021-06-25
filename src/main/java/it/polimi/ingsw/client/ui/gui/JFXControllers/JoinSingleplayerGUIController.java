package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class JoinSingleplayerGUIController extends AbstractGUIController{

    public void goToJoinEnter(ActionEvent actionEvent) {
        startGame(actionEvent);
    }

    public void startGame(ActionEvent actionEvent) {
    }

    public void backToSingle(MouseEvent mouseEvent) {
        change(ScreenName.SINGLEPLAYER);
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
