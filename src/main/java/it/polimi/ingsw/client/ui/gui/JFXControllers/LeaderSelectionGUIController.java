package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.client.ui.controller.LeaderCardSelectionController;
import it.polimi.ingsw.client.ui.gui.LeaderCardSelection;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderSelectionGUIController extends AbstractGUIController implements LeaderCardSelectionController {

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
    }

    public void handleSetupUserMessage(ServerSetupUserMessage message) {
        //se è il messaggio è per me faccioo vedere le carte altrimenti salvo i dati che mi servono e attendo
        if(!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){

            return;
        }

        LeaderCard[] cards = message.getLeaderCards();

        for(int i=0; i<4; i++) {
            ImageView tempImage = new ImageView(new Image("assets/leader_cards/" + cards[i].getId() + ".png", 231, 349, false, false));
            cardList.add(new LeaderCardSelection(cards[i], tempImage));
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

        //TODO devo anche gestire la scelte delle risorse message.getResource()

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
        if(!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
            //model del giocatore corrispondente è stato modificato
            return;
        }

    }

    //TODO
    @Override
    public void handleStatusMessage(StatusMessage message) {

    }
}
