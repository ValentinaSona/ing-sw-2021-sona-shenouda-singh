package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.LeaderCardSelectionController;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.LeaderCardSelection;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderSelectionGUIController extends AbstractGUIController implements LeaderCardSelectionController {

    private List<LeaderCardSelection> cardList;
    private HashMap<Id, Resource> resourceMap;
    private int selectedCards;

    private boolean isChoosing;

    Timeline timeline;

    @FXML
    public ImageView resChoice1, resChoice2, choiceDialog1, choiceDialog2;
    @FXML
    public HBox choiceBox1;
    @FXML
    public HBox choiceBox2;
    @FXML
    private GridPane leaderGrid, devGrid;
    @FXML
    private ImageView loading;
    @FXML
    private GridPane marketGrid;
    @FXML
    private Rectangle marketTray;
    @FXML
    private Label waitLabel;
    @FXML
    private Label choosingLabel;
    @FXML
    private Label choiceLabel;
    @FXML
    private Button marketButton, devButton, setupButton, confirm1, confirm2, confirm;
    @FXML
    private Label res;
    @FXML
    private BorderPane mainPane;

    private Button[] buttons;
    private Map<Node, Integer> choiceMap;
    boolean choiceShowing1, choiceShowing2, clicked1, clicked2;
    private Image baseChoice;

    @FXML
    public void initialize() {

        GUIHelper.getInstance().setCurrentScreen(ScreenName.STARTING_CHOICE);

        selectedCards = 0;
        cardList = new ArrayList<>();
        timeline = GUIHelper.loadingWheel(loading);
        GUIHelper.fillMarket(marketGrid);
        GUIHelper.fillDevGrid(devGrid);

        marketGrid.setOpacity(0);
        marketTray.setOpacity(0);
        devGrid.setOpacity(0);

        buttons = new Button[]{marketButton, devButton, setupButton};

        if( GUIHelper.getInstance().clientIndex() == 0) {
            confirm1.setOpacity(1);
            confirm2.setOpacity(0);
            confirm2.setDisable(true);
            confirm = confirm1;
        }

        else {
            confirm2.setOpacity(1);
            confirm1.setOpacity(0);
            confirm1.setDisable(true);
            confirm = confirm2;
        }

        confirm.setDisable(true);
        confirm.setOpacity(0);

        choiceMap = new HashMap<>();
        choiceMap.put(resChoice1, 2);
        choiceMap.put(resChoice2, 4);

        choiceMap.forEach((key, value) -> disableNode(key));

        showChoice1(false);
        showChoice2(false);

        choiceShowing1 = false;
        choiceShowing2 = false;
        clicked1 = false;
        clicked2 = false;

        isChoosing = false;

        baseChoice = resChoice1.getImage();

        synchronized (DispatcherController.getInstance()) {
            DispatcherController.getInstance().notifyAll();
        }
    }

    public void handleSetupUserMessage(ServerSetupUserMessage message) {
        //se è il messaggio è per me faccioo vedere le carte altrimenti salvo i dati che mi servono e attendo
        Platform.runLater(() -> {
            if(!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
                choosingLabel.setText(message.getUser().getNickName() + " is currently choosing...");
                return;
            }

            isChoosing = true;

            if ( GUIHelper.getInstance().clientIndex() == 0)
                choiceLabel.setText("Select two leader cards");

            else if ( GUIHelper.getInstance().clientIndex() == 3 ) {
                choiceLabel.setText("Select two leader cards and two resources");
                choiceMap.forEach((key, value) -> {
                    key.setDisable(false);
                    key.setOpacity(1);
                });
            }

            else {
                choiceLabel.setText("Select two leader cards and one resource");
                choiceMap.entrySet().stream().filter(e -> e.getValue()==2).forEach(e -> {
                    e.getKey().setDisable(false);
                    e.getKey().setOpacity(1);
                });
            }

            LeaderCard[] cards = message.getLeaderCards();

            waitLabel.setOpacity(0);
            choosingLabel.setOpacity(0);
            loading.setOpacity(0);

            confirm.setOpacity(1);
            confirm.setDisable(false);

            fillLeaderCards(cards);

            setSize();
        });

    }

    public void handleSetupActionMessage(ServerSetupActionMessage message){
        Platform.runLater(() -> {
            if(!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
                return;
            }

            isChoosing = false;
            waitLabel.setText("Choice registered");
            showChoice();

            for(PlayerView p : GameView.getInstance().getPlayers()) {
                if (p.getNickname().equals(MatchSettings.getInstance().getClientNickname()))
                    GUIHelper.getInstance().setClientView(p);
            }
        });

    }

    @Override
    public void handleStatusMessage(StatusMessage message) {

    }

    public void showMarket(ActionEvent actionEvent) {

        adjustButtons();
        marketButton.setMaxWidth(420);

        hideChoice();
        hideDev();

        marketGrid.setOpacity(1);
        marketTray.setOpacity(1);

        choiceLabel.setText("Resource market");

    }

    public void showDevelopment() {

        adjustButtons();
        devButton.setMaxWidth(420);

        hideMarket();
        hideChoice();

        devGrid.setOpacity(1);

        choiceLabel.setText("Development market");

    }

    public void adjustButtons() {
        for (Button b : buttons)
            b.setMaxWidth(380);
    }

    public void showChoice() {

        if (!isChoosing) {
            waitLabel.setOpacity(1);
            choosingLabel.setOpacity(1);
            loading.setOpacity(1);
            leaderGrid.setOpacity(0);
            confirm.setDisable(true);
            confirm.setOpacity(0);
            showChoice1(false);
            showChoice2(false);

            if (clicked1) resChoice1.setImage(baseChoice);
            if (clicked2) resChoice2.setImage(baseChoice);

        }
        else {
            leaderGrid.setOpacity(1);
            confirm.setOpacity(1);
            confirm.setDisable(false);
            if ( GUIHelper.getInstance().clientIndex() == 3 ) {
                choiceMap.forEach((key, value) -> {
                    key.setDisable(false);
                    key.setOpacity(1);
                });
            }

            else if (GUIHelper.getInstance().clientIndex()==1 || GUIHelper.getInstance().clientIndex()==2){
                choiceMap.entrySet().stream().filter(e -> e.getValue()==2).forEach(e -> {
                    e.getKey().setDisable(false);
                    e.getKey().setOpacity(1);
                });
            }
        }


        hideMarket();
        hideDev();

        adjustButtons();
        setupButton.setMaxWidth(420);

        choiceLabel.setText("Setup choice");
    }

    public void fillLeaderCards(LeaderCard[] cards) {
        for(int i=0; i<4; i++) {
            ImageView tempImage = new ImageView(new Image("assets/game/leader_cards/" + cards[i].getId() + ".png", 231, 349, false, false));
            cardList.add(new LeaderCardSelection(cards[i], tempImage));
            leaderGrid.add(tempImage, i, 0);
            int index = i;
            tempImage.setOnMousePressed(event -> {
                if (cardList.get(index).isSelected()) {
                    leaderGrid.getChildren().remove(cardList.get(index).getSelection());
                    cardList.get(index).setSelected(false);
                    selectedCards--;
                }
                else {
                    if (selectedCards < 2) {
                        leaderGrid.add(cardList.get(index).getSelection(), index, 0);
                        cardList.get(index).setSelected(true);
                        selectedCards++;
                    }
                }
            });
        }
    }

    public void hideChoice() {
        confirm.setOpacity(0);
        confirm.setDisable(true);
        leaderGrid.setOpacity(0);
        waitLabel.setOpacity(0);
        choosingLabel.setOpacity(0);
        loading.setOpacity(0);

        choiceMap.forEach((key, value) -> {
            key.setDisable(true);
            key.setOpacity(0);
        });

        choiceBox1.setOpacity(0);
        choiceBox1.setDisable(true);
        choiceBox2.setOpacity(0);
        choiceBox2.setDisable(true);
        choiceDialog1.setOpacity(0);
        choiceDialog2.setOpacity(0);
        res.setOpacity(0);
    }

    public void hideMarket() {
        marketGrid.setOpacity(0);
        marketTray.setOpacity(0);
    }

    public void hideDev() {
        devGrid.setOpacity(0);
    }

    public void confirmChoice(ActionEvent actionEvent) {

        if (selectedCards < 2) {
            choiceLabel.setText("You must choose two leader cards!");
        }

        else if ((GUIHelper.getInstance().clientIndex() == 1 || GUIHelper.getInstance().clientIndex() == 2) && !clicked1) {
            res.setText("You must choose one resource!");
            res.setOpacity(1);
        }
        else if (GUIHelper.getInstance().clientIndex() == 3 && (!clicked1 || !clicked2)) {
            res.setText("You must choose two resources!");
            res.setOpacity(1);
        }

        else {

            resourceMap = new HashMap<>();

            if (GUIHelper.getInstance().clientIndex() == 1 || GUIHelper.getInstance().clientIndex() == 2) {
                resourceMap.put(Id.DEPOT_2, GUIHelper.getInstance().getResFromImage(resChoice1.getImage()));
            }
            else if (GUIHelper.getInstance().clientIndex() == 3 ) {

                var res1 = GUIHelper.getInstance().getResFromImage(resChoice1.getImage());
                var res2 = GUIHelper.getInstance().getResFromImage(resChoice2.getImage());

                if (res1.equals(res2)) {
                    res1.add(res2);
                    resourceMap.put(Id.DEPOT_2, res1);
                }
                else{
                    resourceMap.put(Id.DEPOT_2, res1);
                    resourceMap.put(Id.DEPOT_3, res2);
                }
            }
            UIController.getInstance().chosenStartingResources(resourceMap,
                    cardList.stream().filter(LeaderCardSelection::isSelected).map(LeaderCardSelection::getCard).toArray(LeaderCard[]::new));
        }
    }

    public void chooseRes1(MouseEvent mouseEvent) {
        if (choiceShowing1) {
            choiceDialog1.setDisable(true);
            choiceDialog1.setOpacity(0);
            choiceBox1.setDisable(true);
            choiceBox1.setOpacity(0);
        }
        else {
            choiceDialog1.setDisable(false);
            choiceDialog1.setOpacity(1);
            choiceBox1.setDisable(false);
            choiceBox1.setOpacity(1);
        }

        choiceShowing1 = !choiceShowing1;
    }

    public void chooseRes2(MouseEvent mouseEvent) {
        if (choiceShowing2) {
            choiceDialog2.setDisable(true);
            choiceDialog2.setOpacity(0);
            choiceBox2.setDisable(true);
            choiceBox2.setOpacity(0);
        }
        else {
            choiceDialog2.setDisable(false);
            choiceDialog2.setOpacity(1);
            choiceBox2.setDisable(false);
            choiceBox2.setOpacity(1);
        }
    }

    public void choiceHover1(MouseEvent mouseEvent) {
        if (!clicked1) resChoice1.setImage(((ImageView) mouseEvent.getSource()).getImage());
    }

    public void choiceHover2(MouseEvent mouseEvent) {
        if (!clicked2) resChoice2.setImage(((ImageView) mouseEvent.getSource()).getImage());
    }

    public void resClick2(MouseEvent mouseEvent) {
        resChoice2.setImage(((ImageView) mouseEvent.getSource()).getImage());
        clicked2 = true;
    }

    public void resClick(MouseEvent mouseEvent) {
        resChoice1.setImage(((ImageView) mouseEvent.getSource()).getImage());
        clicked1 = true;
    }

    private void showChoice1(boolean show) {
        show(show, choiceDialog1, choiceBox1, resChoice1);
    }

    private void showChoice2(boolean show) {
        show(show, choiceDialog2, choiceBox2, resChoice2);
    }

    private void show(boolean show, ImageView choiceDialog, HBox choiceBox, ImageView resChoice) {
        if (show) {
            choiceDialog.setDisable(false);
            choiceDialog.setOpacity(1);
            choiceBox.setDisable(false);
            choiceBox.setOpacity(1);
            resChoice.setDisable(false);
            resChoice.setOpacity(1);
        }
        else {
            choiceDialog.setDisable(true);
            choiceDialog.setOpacity(0);
            choiceBox.setDisable(true);
            choiceBox.setOpacity(0);
            resChoice.setDisable(true);
            resChoice.setOpacity(0);
        }
    }

    public void goToGame() {
        Platform.runLater(() -> change(ScreenName.PERSONAL_BOARD));
    }

    private void setSize() {
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setMinWidth(1920);
        stage.setMinHeight(1080);
    }

    public void exitDialog1(MouseEvent mouseEvent) {
        if(!clicked1) resChoice1.setImage(new Image("assets/game/Choose.png"));
    }

    public void exitDialog2(MouseEvent mouseEvent) {
        if(!clicked2) resChoice2.setImage(new Image("assets/game/Choose.png"));
    }
}

