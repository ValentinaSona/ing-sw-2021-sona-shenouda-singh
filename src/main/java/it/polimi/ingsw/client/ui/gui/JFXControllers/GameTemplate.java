package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class GameTemplate {

    private static GameTemplate singleton;

    private VBox tabs;

    private GameTemplate() {
        List<String> players = GUIHelper.getInstance().getOthers();
        tabs = new VBox(20);

        var playerTab = new Button();
        playerTab.setText("Your board");
        playerTab.getStyleClass().add("gameTab");
        playerTab.setMaxWidth(380);
        tabs.getChildren().add(playerTab);

        playerTab.setOnAction(e -> GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD));

        for (String player : players) {
            var tab = new Button();
            tab.setText(player);
            tab.getStyleClass().add("gameTab");
            tab.setMaxWidth(380);
            tabs.getChildren().add(tab);
        }
        tabs.setAlignment(Pos.TOP_RIGHT);
        tabs.setMaxHeight(600);
        StackPane.setAlignment(tabs, Pos.TOP_RIGHT);
        StackPane.setMargin(tabs, new Insets(80, 0, 0, 0));
    }

    public VBox getTabs(ScreenName screen) {
        switch(screen){
            case PERSONAL_BOARD -> ((Button)tabs.getChildren().get(0)).setMaxWidth(420);
            default -> {}
        }

        return tabs;
    }

    public static GameTemplate getInstance() {
        if (singleton == null) singleton = new GameTemplate();
        return singleton;
    }

}
