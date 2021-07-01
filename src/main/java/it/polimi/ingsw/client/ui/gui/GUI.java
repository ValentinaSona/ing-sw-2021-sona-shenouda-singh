package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.ui.Ui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;


public class GUI extends Application implements Ui {

    public void start(){
        System.setProperty("prism.allowhidpi", "false");
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        GUIHelper.getInstance().setBounds(screenBounds);
        GUIHelper.getInstance().setResolution(screenBounds.getHeight() * Screen.getPrimary().getOutputScaleY());

        Parent root = null;
        try {
            URL url = new File("src/main/resources/fxml/mainScreen.fxml").toURI().toURL();
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/mainScreen.fxml"));
            try {
                 root = fxmlLoader.load();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        primaryStage.getIcons().add(new Image("assets/icon.png"));

        primaryStage.setTitle("Client");
        Scene scene = new Scene(root, 1280, 720);

        primaryStage.setMinHeight(720);
        primaryStage.setMinWidth(1280);

        scene.getStylesheets().add("css/mainText.css");
        scene.getStylesheets().add("css/standardBackground.css");
        GUIHelper.getInstance().setCurrentScene(scene);

        primaryStage.setFullScreenExitHint("");
        primaryStage.setScene(scene);

        GUIHelper.getInstance().addStyleSheets(new String[]{
                "buttons.css",
                "gameLog.css",
                "gameTabs.css",
                "list.css",
                "mainText.css",
                "scroll.css",
                "standardBackground.css"
        });

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    /**
     * This method is used to launch the JavaFX application
     */
    public static void launchJavaFX() {
        Application.launch();
    }

}
