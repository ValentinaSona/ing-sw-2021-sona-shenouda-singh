package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import javafx.fxml.FXML;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;

public class WarningGUIController extends AbstractGUIController {

    @FXML
    private Region backRegion;

    @FXML
    public void initialize() {

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
}
