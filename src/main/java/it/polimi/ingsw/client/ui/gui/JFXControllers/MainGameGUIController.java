package it.polimi.ingsw.client.ui.gui.JFXControllers;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainGameGUIController extends AbstractGUIController {

    @FXML
    private StackPane boardPane;

    @FXML
    public void initialize() {

    }

    public void goToMarket(ActionEvent actionEvent) {
        change(ScreenName.MARKET);
    }
}
