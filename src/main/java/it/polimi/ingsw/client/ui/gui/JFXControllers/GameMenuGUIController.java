package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;

public class GameMenuGUIController extends AbstractGUIController implements GameGUIControllerInterface{

    @FXML
    private Region backRegion;

    @FXML
    public void initialize() {
        GUIMessageHandler.getInstance().setCurrentGameController(this);

        var size = new BackgroundSize(1.0, 1.0, true, true, false, false);

        BackgroundImage backIm = new BackgroundImage(GUIHelper.getInstance().getScreenShot(),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                size);

        var background = new Background(backIm);
        backRegion.setBackground(background);
        GaussianBlur blur = new GaussianBlur(20);
        backRegion.setEffect(blur);
    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    @Override
    public void goToMarket() {

    }

    @Override
    public void goToClientBoard() {

    }

    @Override
    public void goToDev() {

    }

    @Override
    public void goToOtherBoard(ActionEvent e) {

    }

    @Override
    public void update() {

    }

    public void saveGame(ActionEvent actionEvent) {
    }

    public void toggleFS(ActionEvent actionEvent) {
    }

    public void leave(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        change(GUIHelper.getInstance().getPrevScreen());
    }
}
