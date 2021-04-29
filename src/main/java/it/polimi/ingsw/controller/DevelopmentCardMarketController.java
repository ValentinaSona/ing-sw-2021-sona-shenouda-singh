package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.DevelopmentCard;
import it.polimi.ingsw.model.DevelopmentCardsMarket;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Resource;

public class DevelopmentCardMarketController {

    private Player currentPlayer;

    private DevelopmentCardsMarket developmentCardsMarket;


    /* Observer method, uncomment once the relevant classes are implemented
       Checks that it is the turn of the player issuing the action,
       Then calls the appropriate method for the action called.
     */

    //public void propertyChangeListener(Action action) {

    //}


    /*  Called when the player selects a development card.
        Checks that the player has not yet made his main action this turn and that they meet the prerequisites for buying that card.
        If so, saves the card temporarily in player.tryToBuy.
     */
    private void getDevelopmentCard(int rowcol) {

    }
    /* Called when the player selects which resources to use to buy the card.
       Calls player.getAvailableResources() to check they possess the resources selected, then withdraws the correct amounts from each location.
       Then removes the card from the DevelopmentMarket, triggering the view that asks the player to choose where to place it.
     */
    private void buyDevelopmentCard(Resource[] fromDepot, Resource[] fromStrongbox) {

    }
    /*  Called when the player selects in which slot to place the card they bought.
        Sets tryToBuy to null when it is done.
     */
    private void placeDevelopmentCard(int position) {

    }


}