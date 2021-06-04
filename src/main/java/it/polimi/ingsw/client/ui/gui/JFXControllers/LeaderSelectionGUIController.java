package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderSelectionGUIController extends AbstractGUIController implements LeaderCardSelectionController {

    private List<LeaderCardSelection> cardList;
    private HashMap<Id, Resource> resourceMap;
    private int selectedCards;

    private boolean isChoosing;

    Timeline timeline;

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

    private Button[] buttons;

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

        isChoosing = false;
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

            else if ( GUIHelper.getInstance().clientIndex() == 3 )
                choiceLabel.setText("Select two leader cards and two resources");

            else
                choiceLabel.setText("Select two leader cards and one resource");

            LeaderCard[] cards = message.getLeaderCards();

            waitLabel.setOpacity(0);
            choosingLabel.setOpacity(0);
            loading.setOpacity(0);

            confirm.setOpacity(1);
            confirm.setDisable(false);

            fillLeaderCards(cards);

            //TODO devo anche gestire la scelte delle risorse message.getResource()
        });

    }

    public void confirmLeader(ActionEvent actionEvent) {
        if (selectedCards == 2){}
            //UIController.getInstance().chosenLeader(
               // cardList.stream().filter(LeaderCardSelection::isSelected).map(LeaderCardSelection::getCard).collect(Collectors.toList()));
        //TODO dopo che ha scelto le leaderCards gli faccio scegliere le risorse
        //cambio scena e gli faccio scegliere le risorse prima però mi salvo temporaneamente le carte selezionata
        //TODO una volta scelte risorse e carte --> //TODO chiamata al metodo del UiController chosenStartingResources(Map<Id,Resource> idResourceMap, LeaderCard[] chosen, User);

    }

    public void handleSetupActionMessage(ServerSetupActionMessage message){
        Platform.runLater(() -> {
            if(!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
                return;
            }

            isChoosing = false;
            waitLabel.setText("Choice registered");

        });

    }

    //TODO
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

    public void showDevelopment(ActionEvent actionEvent) {

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

    public void showChoice(ActionEvent actionEvent) {

        if (!isChoosing) {
            waitLabel.setOpacity(1);
            choosingLabel.setOpacity(1);
            loading.setOpacity(1);
        }
        else {
            leaderGrid.setOpacity(1);
            confirm.setOpacity(1);
            confirm.setDisable(false);
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
    }

    public void hideMarket() {
        marketGrid.setOpacity(0);
        marketTray.setOpacity(0);
    }

    public void hideDev() {
        devGrid.setOpacity(0);
    }

    public void confirmChoice(ActionEvent actionEvent) {

        UIController.getInstance().chosenStartingResources(resourceMap,
                cardList.stream().filter(LeaderCardSelection::isSelected).map(LeaderCardSelection::getCard).toArray(LeaderCard[]::new));
    }
}

