package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;

public abstract class AbstractGUIController implements UiControllerInterface {

    public AbstractGUIController(){
        DispatcherController.getInstance().setCurrentController(this);
    }

    public void change (ScreenName screen) {
        GUIHelper.getInstance().setScreen(screen);
    }

    public void disableNode(Node node) {
        node.setOpacity(0);
        node.setDisable(true);
    }
}
