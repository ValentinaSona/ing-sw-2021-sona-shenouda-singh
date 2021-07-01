package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.client.modelview.*;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.DevelopmentGUIController;
import it.polimi.ingsw.client.ui.gui.JFXControllers.GameGUIControllerInterface;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerFinalScoreMessage;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUIHelper {

    private Rectangle2D bounds;

    private boolean setUpPhase;

    private boolean local;
    private boolean solo;

    private boolean resuming;
    private boolean reconnecting;

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

    private Map<String, Integer> rank;

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

                if (cards[i][j] != null) {

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
                                if (indexI == selectedI && indexJ == selectedJ) {
                                    grid.getChildren().remove(selection);
                                    chosenCard = false;
                                }
                            }

                            Platform.runLater(() -> {
                                ((DevelopmentGUIController)GUIHelper.getInstance().currentGameController).enableBuy(chosenCard);
                            });
                        }
                    });

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

    public void restoreNickList(List<PlayerView> players) {
        nickList = players.stream().map(PlayerView::getNickname).collect(Collectors.toList());
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
        if (name[0].equals("nores")) return new Resource(1, ResourceType.JOLLY);
        else return new Resource(1, ResourceType.valueOf(name[0].toUpperCase()));
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

    /**
     * Changes the current screen to another one, saving both the old screen and the new one
     * @param screen the new scene screen
     */
    public void setScreen(ScreenName screen) {
        Scene scene = GUIHelper.getInstance().getCurrentScene();

        try {
            URL url = new File("src/main/resources/fxml/" + screen.fxml()).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene.setRoot(root);
        } catch (IOException e) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/" + screen.fxml()));
            try {
                Parent root = fxmlLoader.load();
                scene.setRoot(root);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        setPrevScreen(getCurrentScreen());
        setCurrentScreen(screen);
    }

    /**
     * Adds stylesheets to the current scene
     * @param sheets an array containing the names of the stylesheets
     */
    public void addStyleSheets(String[] sheets) {
        Scene scene = getCurrentScene();
        for (String css : sheets) {
            scene.getStylesheets().add("css/" + css);
        }
    }

    private void setPrevScreen(ScreenName prevScreen) {
        this.prevScreen = prevScreen;
    }

    public ImageView getDevVisualizer() {
        devVisualizer.setImage(null);
        chosenCard = false;
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

    /**
     * Places a certain image view on the faith track
     * @param faith the faith value
     * @param faithGrid the GridPane of the faith track
     * @param faithImage the image to be placed
     */
    public void placeFaithTrack(int faith, GridPane faithGrid, ImageView faithImage) {
        if (faith <= 2) faithGrid.add(faithImage, faith, 2);
        else if (faith == 3) faithGrid.add(faithImage, 2, 1);
        else if (faith <= 9) faithGrid.add(faithImage, faith-2, 0);
        else if (faith == 10) faithGrid.add(faithImage, 7, 1);
        else if (faith <= 16) faithGrid.add(faithImage, faith-4, 2);
        else if (faith == 17) faithGrid.add(faithImage, 12, 1);
        else if (faith <= 24) faithGrid.add(faithImage, faith-6, 0);
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

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isSolo() {
        return solo;
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
    }

    public boolean activeSpecialDepot(PlayerView player, int index) {
        var cards = player.getLeaderCards();
        if (cards.size() < index + 1) return  false;
        return cards.get(index) != null && cards.get(index).isActive() && cards.get(index).getSpecialAbility() instanceof ExtraDepotAbility;
    }

    public boolean abilityCorresponds(DepotView depot, LeaderCard card) {
        if (depot != null){
            if (card.getSpecialAbility() instanceof ExtraDepotAbility) {
                var resource = ((ExtraDepotAbility)card.getSpecialAbility()).getType();
                if(depot.getResource() == null) return false;
                return resource == depot.getResource().getResourceType();
            }
            else return false;
        }
        else return false;
    }

    public boolean isResuming() {
        return resuming;
    }

    public void setResuming(boolean resuming) {
        this.resuming = resuming;
        if (resuming) reconnecting = false;
    }

    public boolean isReconnecting() {
        return reconnecting;
    }

    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
        if(reconnecting) resuming = false;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle2D bounds) {
        this.bounds = bounds;
    }

    public void centerScreen(Stage stage) {
        stage.setX((bounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((bounds.getHeight() - stage.getHeight()) / 2);
    }

    public void setFinalScore(ServerFinalScoreMessage message) {
        this.rank = message.getRank().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getNickName(), Map.Entry::getValue));
    }

    public Map<String, Integer> getRank() {
        return rank;
    }
}
