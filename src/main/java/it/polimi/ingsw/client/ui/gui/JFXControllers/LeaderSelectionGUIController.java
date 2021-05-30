package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.UIController;
import it.polimi.ingsw.client.ui.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.LeaderCardSelection;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.LeaderCardsKeeper;
import it.polimi.ingsw.utils.networking.Transmittable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderSelectionGUIController extends AbstractGUIController implements UiControllerInterface {

    private List<LeaderCardSelection> cardList;
    private int selectedCards;


    @FXML
    private GridPane grid;
    @FXML
    private Label choose;
    @FXML
    private Label moreThan2;

    @FXML
    public void initialize() {
        selectedCards = 0;
        cardList = new ArrayList<>();
        LeaderCardsKeeper keeper = new LeaderCardsKeeper();
        updateCards(Arrays.asList(keeper.pickFour()));
    }

    public void updateCards(List<LeaderCard> cards) {

        for(int i=0; i<4; i++) {
            ImageView tempImage = new ImageView(new Image("assets/leader_cards/" + cards.get(i).getId() + ".png", 231, 349, false, false));
            cardList.add(new LeaderCardSelection(cards.get(i), tempImage));
            grid.add(tempImage, i, 0);
            int index = i;
            tempImage.setOnMousePressed(event -> {
                if (cardList.get(index).isSelected()) {
                    grid.getChildren().remove(cardList.get(index).getSelection());
                    cardList.get(index).setSelected(false);
                    selectedCards--;
                }
                else {
                    if (selectedCards < 2) {
                        grid.add(cardList.get(index).getSelection(), index, 0);
                        cardList.get(index).setSelected(true);
                        selectedCards++;
                    }
                }
            });
        }

    }

    public void confirmLeader(ActionEvent actionEvent) {
        if (selectedCards == 2) UIController.getInstance().chosenLeader(this,
                cardList.stream().filter(LeaderCardSelection::isSelected).map(LeaderCardSelection::getCard).collect(Collectors.toList()));
    }

    public void handleLeaderConfirmation() {}

    @Override
    public void handleMessage(Transmittable message) { }
}
