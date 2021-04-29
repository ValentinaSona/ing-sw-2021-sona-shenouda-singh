package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.VaticanReportException;
import it.polimi.ingsw.model.*;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ResourceController extends AbstractController {

 ;

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
    public void addFaithPoints(Player player, Resource faithPoints){
        try{
            FaithTrack faithTrack = player.getFaithTrack();
            faithTrack.addFaithPoints(faithPoints);
        }catch (VaticanReportException vaticanReportException){
            //devo creare tre metodi che gesticano i 3 casi delle pope favor tile
            //ossia prende la posizione di ogni giocatore e vede se gli deve girare la popeFavorTile
            ArrayList<Player> players = getPlayersList();

            for(Player p : players){
                if(!p.equals(player)){
                    FaithTrack faithTrack = p.getFaithTrack();
                    faithTrack.validatePopeFavor(vaticanReportException.getMessage());
                    //vedo la posizione e se è tra 8 e 16 la capovolgo altrimenti
                    //balza e cosi via
                }
            }
        }
    }


    /*  Called when the player ends processing the resources gained from the market.
        Counts the wasted resources, sets tempResources to null and adds faith points to other players if needed.
     */
    private void throwResources() {
        int thrown = 0;
        Resource[] tempResources = getCurrentPlayer().getTempResources();
        Resource faithPoints = null;

        for(Resource resource : tempResources) {
            count += resource.getQuantity();
        }

        faithPoints = new Resource(thrown, ResourceType.FAITH);

        ArrayList<Player> players = getPlayersList();

        for(Player player :  players) {
            if (!player.equals(getCurrentPlayer())) {
                addFaithPoints(player, faithPoints);
            }
        }

    }
    /*

     */
    private void activateProduction(Production production) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        }
    }
}