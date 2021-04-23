package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.MarketMarble;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.ResourceType;

public class MarketController {

    private Market market;

    private Player player;

    private ResourceController resourceController;


    /* Observer method, uncomment once the relevant classes are implemented
       Checks that it is the turn of the player issuing the action,
       Then calls the appropriate method for the action called.
     */

    // public void propertyChangeListener(Action action) {

    // }

    /*  Called when the player selects a row or column of marbles to buy from the market.
        Checks that the player has not yet made his main action this turn and uses the action if not so.
        Calls the market function that returns the gained marbles and handles the twoLeaderCardsException if it arises.
        If it arises, the market has to save internally the temporary array of marbles.
        If it hasn't arisen it calls convertMarbles to turn the gained marbles into resources and saves them in player.tempResources.
     */

    private void buyMarbles(int rowcol) {

    }

    /*  Called when the player has been prompted to choose which resources he wishes to convert his white marbles in as result of twoLeaderCardsException.
        Takes as an input an array of MarketMarbles that it asks the market to substitute to the white marbles in the previous array.
        Passes the new array to convertMarbles, returning to the normal control flow.
     */

    private void convertWhiteMarbles(MarketMarble[] choices) {

    }

    /*  Called on the definitive array of gained market marbles.
        Returns an array of market marbles into an array of resources.
        If faith has been collected, it issues a call to the ResourcesController to add the faith to the user's faith track.
        Saves the resources array (without faith in it) to player.tempResources.
     */
    private void convertMarbles(MarketMarble[] marbles) {

    }

}