package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.TwoLeaderCardsException;
import it.polimi.ingsw.exception.VaticanReportException;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MarketController extends AbstractController {

    private Market market;
    private ResourceController resourceController;


    /*
       Checks that it is the turn of the player issuing the action,
       Then calls the appropriate method for the action called.
     */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //mi preocuppo di implemetare questo metodo per tutti i controller
        //una volta visto il funzionamento della view.
        //per non ripetere codice i controlli sul giocatore vengono fatti qui dentro prima di arrivare
        //allo switch
        switch(evt.getPropertyName()){
            case "BUY_MARBLES":
                buyMarbles(getCurrentPlayer(),(int)evt.getNewValue());
                break;
            case "CONVERT_WHITE_MARBLES":
                convertMarbles((MarketMarble[]) evt.getNewValue());
                break;
            default:
                throw new RuntimeException("No such Event exception");
        }
    }

    /*  Called when the player selects a row or column of marbles to buy from the market.
        Checks that the player has not yet made his main action this turn and uses the action if not so.
        Calls the market function that returns the gained marbles and handles the twoLeaderCardsException if it arises.
        If it arises, the market has to save internally the temporary array of marbles.
        If it hasn't arisen it calls convertMarbles to turn the gained marbles into resources and saves them in player.tempResources.
     */

    private void buyMarbles(Player player, int rowcol) {

        //getCurrentPlayer().useMainAction();
        try{
            MarketMarble[] marbles = market.getResources(player,rowcol);

            convertMarbles(marbles);
        }catch(TwoLeaderCardsException twoLeaderCardsException){
            //if the currentPlayer does not posses 2 LeaderCards the marbles are
            //returned without throwing any exception otherwise an exception is thrown
            //and the currentPlayer is notified that he has to choose how to convert the white
            //marbles using his two leaderCards
        }

    }

    /*  Called when the player has been prompted to choose which resources he wishes to convert his white marbles in as result of twoLeaderCardsException.
        Takes as an input an array of MarketMarbles that it asks the market to substitute to the white marbles in the previous array.
        Passes the new array to convertMarbles, returning to the normal control flow.
     */

    private void convertWhiteMarbles(MarketMarble[] choices) {
        MarketMarble[] marbles = market.getChosen(choices);
        convertMarbles(marbles);
    }

    /*  Called on the definitive array of gained market marbles.
        Returns an array of market marbles into an array of resources.
        If faith has been collected, it issues a call to the ResourcesController to add the faith to the user's faith track.
        Saves the resources array (without faith in it) to player.tempResources.
     */
    private void convertMarbles(MarketMarble[] marbles) {
        ArrayList<Resource> temp = new ArrayList<>();
        Resource faithPoints = null;

        for(ResourceType type : ResourceType.values()){
            int count = 0;
            for(MarketMarble marble : marbles){
                if(marble.convertToResource() == type)
                    count++;
            }
            if(count != 0)
                temp.add(new Resource(count, type));
        }
        //controlling if there is a faith resource
        for(Resource res : temp){
            if(res.getResourceType() == ResourceType.FAITH){
                faithPoints = res;
                temp.remove(res);
                break;
            }
        }

        //if faithPoints is different from null
        if(faithPoints != null){
            resourceController.addFaithPoints(getCurrentPlayer(), faithPoints);
        }

        Resource[] tempResources =(Resource[]) temp.toArray();
        getCurrentPlayer().setTempResources(tempResources);
    }

}