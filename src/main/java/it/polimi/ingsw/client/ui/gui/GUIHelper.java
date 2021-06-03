package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.DevelopmentCard;
import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

public class GUIHelper {

    private Background background;
    private Scene currentScene;
    private List<String> nickList;
    int clientIndex;
    ScreenName currentScreen;

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

    public static Timeline loadingWheel(ImageView loadingWheel) {

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), ev -> {
            RotateTransition rotate = new RotateTransition();
            rotate.setAxis(Rotate.Z_AXIS);
            rotate.setByAngle(360);
            rotate.setCycleCount(1);
            rotate.setDuration(Duration.millis(1000));
            rotate.setInterpolator(Interpolator.LINEAR);
            rotate.setNode(loadingWheel);
            rotate.play();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        return timeline;
    }

    public static void fillMarket(GridPane grid) {

        MarketView market = GameView.getInstance().getMarketInstance();
        grid.getChildren().clear();

        for (int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {

                grid.add(new ImageView(new Image("assets/market/" + market.getTray()[i][j].toString().toLowerCase() + ".png", 120, 120, false, false)), j, i+1);

            }
        }

        grid.add(new ImageView(new Image("assets/market/" + market.getExtra().toString().toLowerCase() + ".png", 120, 120, false, false)), 4, 0);
    }

    public static void fillDevGrid (GridPane grid) {

        DevelopmentCard[][] cards = GameView.getInstance().getDevelopmentCardsMarket().getTray();

        for(int i = 2; i >= 0; i--) {
            for(int j = 0; j < 4; j++) {

                grid.add(new ImageView(new Image("assets/game/development_cards/" + cards[i][j].getId() + ".png", 173, 261, false, false)), j, i);

            }
        }

    }

    public void buildNickList(List<User> users) {
        nickList = users.stream().map(User::getNickName).collect(Collectors.toList());
        clientIndex = nickList.indexOf(MatchSettings.getInstance().getClientNickname());
    }

    public List<String> getNickList() {
        return nickList;
    }

    public int clientIndex() {
        return clientIndex;
    }

    public ScreenName getCurrentScreen() {
        return currentScreen;
    }

    public void setCurrentScreen(ScreenName currentScreen) {
        this.currentScreen = currentScreen;
    }
}
