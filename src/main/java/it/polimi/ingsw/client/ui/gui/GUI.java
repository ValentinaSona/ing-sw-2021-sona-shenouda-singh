package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.Ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class GUI extends Application implements Ui {

    public void start(){
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        GUIHelper.getInstance().setResolution(screenBounds.getHeight());
        URL url = new File("src/main/resources/fxml/mainScreen.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Client");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("css/mainText.css");
        scene.getStylesheets().add("css/standardBackground.css");
        GUIHelper.getInstance().setCurrentScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);

        primaryStage.setOnCloseRequest(e -> Platform.exit());

        primaryStage.show();
    }

    /**
     * This method is used to launch the JavaFX application
     */
    public static void launchJavaFX() {
        Application.launch();
    }

}
