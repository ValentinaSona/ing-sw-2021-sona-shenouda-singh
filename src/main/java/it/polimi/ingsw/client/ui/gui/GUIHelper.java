package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.GameGUIControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class GUIHelper {

    private Background background;
    private Scene currentScene;
    private List<String> nickList;
    private List<String> others;
    private int clientIndex;
    private ScreenName currentScreen;
    private PlayerView clientView;
    private UiControllerInterface currentController;
    private GameGUIControllerInterface currentGameController;

    private static GUIHelper singleton;

    private GUIHelper() {}

    public static GUIHelper getInstance() {
        if (singleton == null) singleton = new GUIHelper();
        return singleton;
    }

    public void setCurrentController(UiControllerInterface currentController) {
        this.currentController = currentController;
    }

    public void setCurrentGameController(GameGUIControllerInterface controller) {
        currentGameController = controller;
    }

    public GameGUIControllerInterface getCurrentGameController() {
        return currentGameController;
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

                if (cards[i][j] != null)
                    grid.add(new ImageView(new Image("assets/game/development_cards/" + cards[i][j].getId() + ".png", 187, 281, false, false)), j, i);

            }
        }

    }

    public void buildNickList(List<User> users) {
        nickList = users.stream().map(User::getNickName).collect(Collectors.toList());
        clientIndex = nickList.indexOf(MatchSettings.getInstance().getClientNickname());
        others = nickList.stream().filter(e -> !e.equals(MatchSettings.getInstance().getClientNickname())).collect(Collectors.toList());
    }

    public List<String> getNickList() {
        return nickList;
    }

    public List<String> getOthers() {
        return others;
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

    public Resource getResFromImage(Image image) {
        String[] path = image.getUrl().split("/");
        String[] name = path[path.length-1].split("\\.");
        return new Resource(1, ResourceType.valueOf(name[0].toUpperCase()));
    }

    public void setClientView(PlayerView p) {
        clientView = p;
    }

    public PlayerView getClientView() {
        return clientView;
    }

    public Image getImage(ResourceType res, int x, int y) {
        return new Image("assets/game/resources/" + res.toString().toLowerCase() + ".png", x, y, false, false);
    }

    public Image getImage(MarketMarble marble, int x, int y) {
        return new Image("assets/game/marbles/" + marble.toString().toLowerCase() + ".png", x, y, false, false);
    }

    public void setScreen(ScreenName screen) {
        Scene scene = GUIHelper.getInstance().getCurrentScene();

        try {
            URL url = new File("src/main/resources/fxml/" + screen.fxml()).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] stylesheets = screen.css();

        for (String css : stylesheets) {
            scene.getStylesheets().add("css/" + css);
        }

        GUIHelper.getInstance().setCurrentScreen(screen);
    }
}
