package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.DevelopmentGUIController;
import it.polimi.ingsw.client.ui.gui.JFXControllers.GameGUIControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIHelper {

    private boolean setUpPhase;

    private Background background;
    private Scene currentScene;
    private List<String> nickList;
    private List<String> others;
    private int clientIndex;
    private ScreenName currentScreen;
    private PlayerView clientView;
    private UiControllerInterface currentController;
    private GameGUIControllerInterface currentGameController;
    private ImageView devVisualizer;
    private String selectedPlayer;
    private Image screenshot;

    private static GUIHelper singleton;

    private double screenHeight;
    private ScreenName prevScreen;
    private boolean turn;
    private boolean choosingTemp;

    private boolean chosenCard;
    private int selectedI, selectedJ;
    private boolean selectSlot;

    private Id selectedSlot;

    CurrAction currAction;

    private GUIHelper() {
        currAction = CurrAction.IDLE;
        selectSlot = false;
        choosingTemp = false;
        chosenCard = false;
        devVisualizer = new ImageView();
        devVisualizer.setFitWidth(400);
        devVisualizer.setPreserveRatio(true);
        StackPane.setMargin(devVisualizer, new Insets(0, 0, 0, 1000));

        setUpPhase = true;
    }

    public static GUIHelper getInstance() {
        if (singleton == null) singleton = new GUIHelper();
        return singleton;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public boolean getTurn() {
        return turn;
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

                if (cards[i][j] != null) {
                    grid.add(new ImageView(getInstance().getImage(cards[i][j], 187, 281)), j, i);
                }

            }
        }

    }

    public void updateDevGrid (GridPane grid) {

        DevelopmentCard[][] cards = GameView.getInstance().getDevelopmentCardsMarket().getTray();

        grid.getChildren().clear();

        for(int i = 2; i >= 0; i--) {
            for(int j = 0; j < 4; j++) {

                var tempIm = getInstance().getImage(cards[i][j]);
                var tempView = new ImageView(tempIm);

                tempView.setFitWidth(GUISizes.get().devSize());
                tempView.setPreserveRatio(true);

                tempView.setOnMouseEntered(e -> { if (!chosenCard) devVisualizer.setImage(tempIm);});

                int indexI = i;
                int indexJ = j;

                var selection = new ImageView(new Image("assets/game/development_cards/card_selection.png"));
                selection.setFitWidth(GUISizes.get().devSize());
                selection.setPreserveRatio(true);


                tempView.setOnMouseReleased(e -> {
                    if(getInstance().getTurn()) {
                        if (!chosenCard) {
                            grid.add(selection, indexJ, indexI);
                            selectedI = indexI;
                            selectedJ = indexJ;
                            chosenCard = true;
                        }
                        else {
                            grid.getChildren().remove(selection);
                            chosenCard = false;
                        }

                        Platform.runLater(() -> {
                            ((DevelopmentGUIController)GUIHelper.getInstance().currentGameController).enableBuy(chosenCard);
                        });
                    }
                });

                if (cards[i][j] != null) {
                    grid.add(tempView, j, i);
                }

            }
        }

        grid.setOnMouseExited(e -> { if (!chosenCard) devVisualizer.setImage(null);});

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

    public Image getImage(DevelopmentCard card, int x, int y) {
        return new Image("assets/game/development_cards/" + card.getId() + ".png", x, y, false, false);
    }

    public Image getImage(DevelopmentCard card) {
        return new Image("assets/game/development_cards/" + card.getId() + ".png");
    }

    public Image getImage(LeaderCard card, int x, int y) {
        return new Image("assets/game/leader_cards/" + card.getId() + ".png", x, y, false, false);
    }

    public Image getImage(PopeFavorTiles tile, int index) {
        if (tile == PopeFavorTiles.DISMISSED) return null;
        return new Image("assets/game/pope_tiles/" + index + "_" + tile.toString().toLowerCase() + ".png", GUISizes.get().popeTile(), GUISizes.get().popeTile(), false, false);
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
        setPrevScreen(getCurrentScreen());
        setCurrentScreen(screen);
    }

    private void setPrevScreen(ScreenName prevScreen) {
        this.prevScreen = prevScreen;
    }

    public ImageView getDevVisualizer() {
        return devVisualizer;
    }

    public PlayerView getSelectedPlayer() {

        var players = GameView.getInstance().getPlayers();
        PlayerView selected = getInstance().getClientView();
        for(PlayerView p : players) {
            if(p.getNickname().equals(selectedPlayer)) selected = p;
        }
        return selected;
    }

    public void setSelectedPlayer(Object source) {
        this.selectedPlayer = ((Button)source).getText();
    }

    public void setResolution(double height) {
        this.screenHeight = height;
    }

    public double getResolution () {
        return screenHeight;
    }

    public void setScreenshot(Image image) {
        screenshot = image;
    }

    public Image getScreenShot() {
        return screenshot;
    }

    public ScreenName getPrevScreen() {
        return prevScreen;
    }

    public boolean isSetUpDone() {
        return setUpPhase;
    }

    public void setSetUpDone (boolean setUpPhase) {
        this.setUpPhase = setUpPhase;
    }

    public Image getResourceImage(MarketMarble m, int x, int y) {
        if (m != MarketMarble.WHITE) return getImage(m.convertToResource(), x, y);
        else return new Image("assets/game/resources/nores.png", x, y, false, false);
    }

    public boolean isChoosingTemp() {
        return choosingTemp;
    }

    public void setChoosingTemp(boolean choosingTemp) {
        this.choosingTemp = choosingTemp;
    }

    public int getSelectedI() {
        return selectedI;
    }

    public int getSelectedJ() {
        return selectedJ;
    }

    public CurrAction getCurrAction() {
        return currAction;
    }

    public void setCurrAction(CurrAction currAction) {
        this.currAction = currAction;
    }

    public Id getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(Id selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public void setChosenCard(boolean chosenCard) {
        this.chosenCard = chosenCard;
    }

    public Image getAbilityImageFromLeader(LeaderCard card) {
        return new Image("assets/game/leader_cards/abilities/" + card.getId() + ".png", GUISizes.get().abilityX(), GUISizes.get().abilityY(), false, false);
    }
}
