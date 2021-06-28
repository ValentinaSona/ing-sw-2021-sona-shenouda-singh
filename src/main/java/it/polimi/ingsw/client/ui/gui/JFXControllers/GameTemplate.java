package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class GameTemplate {

    private static GameTemplate singleton;

    private VBox tabsPlayer;
    private VBox tabsMarket;

    private GameTemplate() {
        List<String> players = GUIHelper.getInstance().getOthers();
        tabsPlayer = new VBox(20);

        var playerTab = new Button();
        playerTab.setText("Your board");
        playerTab.getStyleClass().add("gameTab");
        playerTab.setMaxWidth(380);
        tabsPlayer.getChildren().add(playerTab);

        playerTab.setOnAction(e -> GUIHelper.getInstance().setScreen(ScreenName.PERSONAL_BOARD));

        for (String player : players) {
            var tab = new Button();
            tab.setText(player);
            tab.getStyleClass().add("gameTab");
            tab.setMaxWidth(380);
            tabsPlayer.getChildren().add(tab);

            tab.setOnAction(e -> GUIHelper.getInstance().getCurrentGameController().goToOtherBoard(e));

        }
        tabsPlayer.setAlignment(Pos.TOP_RIGHT);
        tabsPlayer.setMaxHeight(600);
        StackPane.setAlignment(tabsPlayer, Pos.TOP_RIGHT);
        StackPane.setMargin(tabsPlayer, new Insets(80, 0, 0, 0));

        //tabsMarket = new VBox(20);

        var marketButton = new Button();
        marketButton.setText("Resource Market");
        marketButton.setId("marketButton");
        marketButton.setMaxWidth(380);

        marketButton.setOnAction(e -> GUIHelper.getInstance().getCurrentGameController().goToMarket());

        VBox.setMargin(marketButton, new Insets(40, 0, 0, 0));

        var devButton = new Button();
        devButton.setText("Development market");
        devButton.setId("devButton");
        devButton.setMaxWidth(380);

        devButton.setOnAction(e -> GUIHelper.getInstance().getCurrentGameController().goToDev());

        tabsPlayer.getChildren().add(marketButton);
        tabsPlayer.getChildren().add(devButton);

        //tabsMarket.getChildren().add(marketButton);
        //tabsMarket.getChildren().add(devButton);

        //tabsMarket.setAlignment(Pos.TOP_RIGHT);
        //tabsMarket.setMaxHeight(350);
        //tackPane.setAlignment(tabsMarket, Pos.BOTTOM_RIGHT);
    }

    public VBox getPlayersTabs() {
        return tabsPlayer;
    }

    public VBox getMarketsTabs() {
        return tabsMarket;
    }

    public void setTabs(ScreenName screen) {

        tabsPlayer.getChildren().forEach(e -> ((Button)e).setMaxWidth(380));
        //tabsMarket.getChildren().forEach(e -> ((Button)e).setMaxWidth(380));

        switch(screen){
            case PERSONAL_BOARD -> ((Button) tabsPlayer.getChildren().get(0)).setMaxWidth(420);
            case MARKET -> {
                if (!GUIHelper.getInstance().isSolo())
                    ((Button) tabsPlayer.getChildren().get(MatchSettings.getInstance().getTotalUsers())).setMaxWidth(420);
                else ((Button) tabsPlayer.getChildren().get(1)).setMaxWidth(420);
            }
            case DEV_MARKET -> {
                if (!GUIHelper.getInstance().isSolo())
                    ((Button) tabsPlayer.getChildren().get(MatchSettings.getInstance().getTotalUsers()+1)).setMaxWidth(420);
                else ((Button) tabsPlayer.getChildren().get(2)).setMaxWidth(420);
            }
            case OTHER_BOARD -> ((Button) tabsPlayer.getChildren().get(GUIHelper.getInstance().getOthers().indexOf(GUIHelper.getInstance().getSelectedPlayer().getNickname())+1)).setMaxWidth(420);
            default -> {}
        }

    }

    public static GameTemplate getInstance() {
        if (singleton == null) singleton = new GameTemplate();
        return singleton;
    }

}
