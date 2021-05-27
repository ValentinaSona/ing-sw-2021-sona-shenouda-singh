package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.LeaderCardsKeeper;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LeaderSelectionGUIController extends AbstractGUIController {

    HashMap<ImageView, LeaderCard> cardMap;
    @FXML
    private GridPane grid;

    @FXML
    public void initialize() {
        cardMap = new HashMap<>();
        LeaderCardsKeeper keeper = new LeaderCardsKeeper();
        updateCards(Arrays.asList(keeper.pickFour()));
    }

    public void updateCards(List<LeaderCard> cards) {

        for(int i=0; i<4; i++) {
            ImageView tempImage = new ImageView(new Image("assets/leader_cards/" + cards.get(i).getId() + ".png", 231, 349, false, false));
            cardMap.put(tempImage, cards.get(i));
            grid.add(tempImage, i, 0);
        }

    }
}
