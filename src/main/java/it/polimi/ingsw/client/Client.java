package it.polimi.ingsw.client;


import it.polimi.ingsw.client.ui.Ui;


public class Client {

    private final Ui chosenUi;

    public Client(Ui uiInterface){
        this.chosenUi = uiInterface;
    }

    public Ui getChosenUi() {
        return chosenUi;
    }
}
