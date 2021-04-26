package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Production;
import it.polimi.ingsw.model.Resource;

public class ResourceController {

    private Player currentPlayer;

    private Player[] playerList;

    /* Observer method, uncomment once the relevant classes are implemented
       Checks that it is the turn of the player issuing the action,
       Then calls the appropriate method for the action called.
       May call player.tidyWarehouse and player.depositIntoWarehouse alongside its private methods.
     */

    // public void propertyChangeListener(Action action) {

    // }


    /* Public method called by this and other controllers when they need to add faith points to a player's faith track.
       Calls the faith track method with the same name and handles the VaticanReport exception by calling the vaticanReport method.
     */
    public void addFaithPoints(Resource faith, Player player) {

    }
    /* Called when someone crosses a pope space in their faith track.
       Checks each player's position and modifies the popeFavorTiles accordingly.
     */
    private void vaticanReport(int popeSpace) {

    }

    /*  Called when the player ends processing the resources gained from the market.
        Counts the wasted resources, sets tempResources to null and adds faith points to other players if needed.
     */
    private void throwResources() {

    }
    /*

     */
    private void activateProduction(Production production) {

    }

}