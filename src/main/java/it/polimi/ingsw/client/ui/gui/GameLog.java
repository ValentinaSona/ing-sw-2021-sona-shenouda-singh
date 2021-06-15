package it.polimi.ingsw.client.ui.gui;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.LinkedList;
import java.util.List;

public class GameLog {

    private static GameLog singleton;
    private List<String> text;

    public static GameLog getInstance() {
        if (singleton == null) singleton = new GameLog();
        return singleton;
    }

    private GameLog() {
        text = new LinkedList<>();
    }

    public void update(TextFlow log) {
        log.getChildren().add(new Text("It's jimmy's turn!"));
    }
}
