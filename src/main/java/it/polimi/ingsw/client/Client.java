package it.polimi.ingsw.client;


import it.polimi.ingsw.client.ui.UIController;
import it.polimi.ingsw.client.ui.Ui;


public class Client {

    private final Ui chosenUi;

    public Client(Ui uiInterface){
        this.chosenUi = uiInterface;
    }

    public void run(){
        while(true){
            try{
                UIController.getInstance().processServerMessage();
            }catch (Exception e){
                //TODO gestione disconnessioni
                e.printStackTrace();
            }
        }
    }

    public Ui getChosenUi() {
        return chosenUi;
    }
}
