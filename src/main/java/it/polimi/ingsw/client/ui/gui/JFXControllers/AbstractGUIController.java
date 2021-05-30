package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractGUIController {

    private Parent root;

    @FXML
    private StackPane mainPane;

    public void change (ScreenName screen, MouseEvent event) {

        Scene scene1 = ((Node) event.getSource()).getScene();

        changeScene(scene1, screen);
    }

    public void change (ScreenName screen, ActionEvent event) {

        Scene scene1 = ((Node) event.getSource()).getScene();

        changeScene(scene1, screen);
    }

    private void changeScene (Scene scene, ScreenName screen) {
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
