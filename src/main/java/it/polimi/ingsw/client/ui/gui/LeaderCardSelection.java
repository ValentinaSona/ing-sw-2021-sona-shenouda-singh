package it.polimi.ingsw.client.ui.gui;

import it.polimi.ingsw.server.model.LeaderCard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LeaderCardSelection {
    private final LeaderCard card;
    private final ImageView image;
    private boolean isSelected;
    private final ImageView selection;

    public LeaderCardSelection(LeaderCard card, ImageView image) {
        this.card = card;
        this.image = image;
        isSelected = false;
        selection = new ImageView(new Image("assets/game/leader_cards/card_selection.png"));
    }

    public boolean isSelected() {
        return isSelected;
    }

    public LeaderCard getCard() {
        return card;
    }

    public ImageView getImage() {
        return image;
    }

    public ImageView getSelection() {
        return selection;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
