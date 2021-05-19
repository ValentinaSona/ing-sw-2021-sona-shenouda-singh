package it.polimi.ingsw.client.ui.gui.GUIControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractGUIController {

    @FXML
    private Pane mainPane;

    public void change(String sceneName, String css) {

        Parent loaded = null;
        try {
            URL url = new File("src/main/resources/fxml/" + sceneName).toURI().toURL();
            loaded = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (loaded == null) throw new RuntimeException("Something went wrong with FXML loading");

        Scene scene = new Scene(loaded);

        scene.getStylesheets().add("css/" + css);

        Stage stage = (Stage) mainPane.getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

}
