package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractGUIController implements UiControllerInterface {

    private Parent root;

    public AbstractGUIController(){
        UIController.getInstance().setCurrentController(this);
    }
    @FXML
    private StackPane mainPane;

    public void change (ScreenName screen) {

        Scene scene = GUIHelper.getInstance().getCurrentScene();

        try {
            URL url = new File("src/main/resources/fxml/" + screen.fxml()).toURI().toURL();
            root = FXMLLoader.load(url);
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
