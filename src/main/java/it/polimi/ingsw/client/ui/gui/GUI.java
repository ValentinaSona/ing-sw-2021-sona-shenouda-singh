package it.polimi.ingsw.client.ui.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL url = new File("src/main/resources/fxml/mainScreen.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Client");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/mainText.css");
        scene.getStylesheets().add("css/standardBackground.css");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * This method is used to launch the JavaFX application
     */
    public static void launchJavaFX() {
        Application.launch();
    }

}
