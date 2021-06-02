package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractGUIController implements UiControllerInterface {

    public AbstractGUIController(){
        DispatcherController.getInstance().setCurrentController(this);
    }

    public void change (ScreenName screen) {

        Scene scene = GUIHelper.getInstance().getCurrentScene();

        try {
            URL url = new File("src/main/resources/fxml/" + screen.fxml()).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] stylesheets = screen.css();

        for (String css : stylesheets) {
            scene.getStylesheets().add("css/" + css);
        }
    }
}
