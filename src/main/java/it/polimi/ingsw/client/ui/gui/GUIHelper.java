package it.polimi.ingsw.client.ui.gui;

import javafx.scene.Scene;
import javafx.scene.layout.*;

public class GUIHelper {

    private Background background;
    private Scene currentScene;

    private static GUIHelper singleton;

    private GUIHelper() {}

    public static GUIHelper getInstance() {
        if (singleton == null) singleton = new GUIHelper();
        return singleton;
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
