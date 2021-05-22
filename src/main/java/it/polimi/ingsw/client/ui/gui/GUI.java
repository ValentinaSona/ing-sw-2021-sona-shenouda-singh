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
        scene.getStylesheets().add("css/style1.css");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * This method is used to launch the JavaFX application
     */
    public static void launchJavaFX() {
        Application.launch();
    }

}
