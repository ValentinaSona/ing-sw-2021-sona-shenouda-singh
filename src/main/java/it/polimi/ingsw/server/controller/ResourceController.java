package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.Depot;
import it.polimi.ingsw.server.model.observable.DevelopmentCardSlot;
import it.polimi.ingsw.server.model.observable.FaithTrack;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConfirmProductionMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientDepositIntoWarehouseMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientDepositResourceIntoSlotMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientTidyWarehouseMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ResourceController extends AbstractController {
    private static ResourceController singleton;


    private ResourceController(Model model){
        super(model);
    }

    public static ResourceController getInstance(Model model){
        if(singleton == null){
            singleton = new ResourceController(model);
        }

        return  singleton;

    }


    /* ****************************************************
     * Utility methods invoked by controllers themselves. *
     ******************************************************/

    /**
     *  Public method called all controllers when they need to add faith points to a player's faith track.
     *  Calls the faithTrack's method by the same name and handles the VaticanReport exception by calling the validatePopeFavor method on every player.
     *  This method doesn't perform any check on the caller since it's called only by other controllers' methods that have already performed their own checks.
     * @param player Player who is gaining the additional faith points.
     * @param faithPoints Resource to be added to the faith track.
     */
    public void addFaithPoints(Player player, Resource faithPoints){
        try{
            FaithTrack faithTrack = player.getFaithTrack();
            faithTrack.addFaithPoints(faithPoints);
        }catch (VaticanReportException vaticanReportException){

            ArrayList<Player> players = getPlayersList();
            players.remove(player);

            for(Player p : players){
                FaithTrack faithTrack = p.getFaithTrack();
                faithTrack.validatePopeFavor(vaticanReportException.getMessage());
            }
        }
    }


    /** TODO: check exception source
     * Subtracts resources from the strongbox or the warehouse as part of the process in which the user selects how to pay for a production/development card.
     * If the attempt to buy does not succeed, the resetResources method is invoked to place them back.
     * @param player Player whose resources are being managed.
     * @param resource Resource being paid.
     * @param resourceId Source from where the resources are being taken.
     * @throws InvalidDepotException The selected Id does not contain the selected resource.
     */
    private void selectResources(Player player , Resource resource, Id resourceId) throws InvalidDepotException {


        ArrayList<Id> warehouseIds = (ArrayList<Id>) Arrays.asList(Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3, Id.S_DEPOT_1, Id.S_DEPOT_2);

        if(warehouseIds.contains(resourceId)){
            player.getWarehouse().get(resourceId.getValue()).subtractResource(resource);
        }else if(resourceId == Id.STRONGBOX){
            player.getStrongbox().subResources(resource);
        }else {
            throw new RuntimeException("Supplied ResourceId doesn't belong to warehouse or strongbox.");
        }

    }

    /**
     * Puts back the resources into strongbox/warehouse in case an attempt to buy something as failed.
     * @param player Player whose resources are being managed.
     * @param resourceHashMap Resources and the origin at which they must be returned.
     * @throws InvalidDepotException The previously supplied resources are somehow invalid.
     */
    public void resetResources(Player player, HashMap<Id, Resource> resourceHashMap) throws InvalidDepotException {
        ArrayList<Depot> warehouse = player.getWarehouse();

        ArrayList<Id> warehouseIds = (ArrayList<Id>) Arrays.asList(Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3, Id.S_DEPOT_1, Id.S_DEPOT_2);

        for(Id id : resourceHashMap.keySet()){
            if(warehouseIds.contains(id)){
                warehouse.get(id.getValue()).addResource(resourceHashMap.get(id));
                resourceHashMap.remove(id);
            }else if(id == Id.STRONGBOX){
                player.getStrongbox().addResources(resourceHashMap.get(id));
                resourceHashMap.remove(id);
            }else {
                throw new RuntimeException("Supplied ResourceId doesn't belong to warehouse or strongbox.");
            }
        }
    }


    /* ******************************************
     * Handler methods invoked by user actions. *
     ********************************************/

    /**
     *  Called when the player ends processing the resources gained from the market.
     *  Counts the wasted resources, sets tempResources to null and adds faith points to other players if needed.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void throwResources( RemoteViewHandler view, User user) {
        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            int thrown = 0;
            ArrayList<Resource> tempResources = player.getTempResources();
            Resource faithPoints;


            for(Resource resource : tempResources) {
                thrown += resource.getQuantity();
            }

            player.dumpTempResources();

            faithPoints = new Resource(thrown, ResourceType.FAITH);
            ArrayList<Player> players = getPlayersList();
            players.remove(player);

            for(Player p :  players) {
                addFaithPoints(p, faithPoints);
            }
            view.handleStatusMessage(StatusMessage.OK);
        }
    }

    /**
     * Called when a player want to withdraw Resources from a depot in order
     * to put them in an other depot or if he want to throw them
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void tidyWarehouse(ClientTidyWarehouseMessage action, RemoteViewHandler view, User user){
        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {


            try {


                Depot fromDepot = player.getWarehouse().get(action.getFrom().getValue());
                Depot toDepot = player.getWarehouse().get(action.getTo().getValue());


                Resource fromResource = fromDepot.getResource();
                Resource toResource = toDepot.getResource();


                if (toDepot.getResource() == null || toDepot.getResource().getQuantity() == 0) {
                    //if i am trying to move resources to an empty specialDepot or a empty normal depot
                    toDepot.addResource(fromResource);
                    //if no exception is thrown
                    fromDepot.subtractResource(toResource);
                    view.handleStatusMessage(StatusMessage.OK);
                } else {
                    if (fromResource.getQuantity() <= toDepot.getCapacity() && toResource.getQuantity() <= fromDepot.getCapacity()) {
                        //i can switch the content of the depot
                        toDepot.subtractResource(toResource);
                        fromDepot.subtractResource(fromResource);

                        toDepot.addResource(fromResource);
                        fromDepot.addResource(toResource);
                        view.handleStatusMessage(StatusMessage.OK);
                    } else {
                        view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
                    }
                }


            } catch (InvalidDepotException invalidDepotException) {
                //if i enter here it means that the action on the warehouse is failed
                //and the player has been already informed
            }
        }
    }

    /**
     * Called when a player is depositing Resources from the tempResources acquired from the market in one of his depots.
     * Removes the selected resource from tempResources.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void depositIntoWarehouse(ClientDepositIntoWarehouseMessage action, RemoteViewHandler view, User user){


        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {


            try {
                ArrayList<Depot> warehouse = player.getWarehouse();
                Depot targetDepot = warehouse.get(action.getSlotId().getValue());

                Id[] standardDepots = {Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3};

                // If the deposit is not empty and it contains a resource of a different type than the one we are trying to deposit the action is illegal. Redundant check.
                if (targetDepot.getResource() != null && targetDepot.getResource().getResourceType() != action.getResource().getResourceType()) {
                    throw new InvalidDepotException();
                }

                for (Id depotId : standardDepots) {
                    if (depotId != action.getSlotId()) {
                        if (warehouse.get(depotId.getValue()).getResource() != null && warehouse.get(depotId.getValue()).getResource().getResourceType() == action.getResource().getResourceType()) {

                            // If one of the other standard depots isn't empty and contains a resource of the same type as the one we're trying to deposit the action is illegal.
                            throw new InvalidDepotException();
                        }
                    }
                }

                // If the targetDepot is a special depot the addResources already performs all the checks needed.

                // If it is not illegal to deposit, i can add to the resource to the target deposit.
                targetDepot.addResource(action.getResource());

                // If the deposit fails the InvalidDepotException is thrown by addResources and the next statement is never executed, leaving the resources still to be deposited.
                getCurrentPlayer().subFromTempResources(action.getResource());
                view.handleStatusMessage(StatusMessage.CONTINUE);


            } catch (InvalidDepotException invalidDepotException) {
                //the depot model has already notified the player of the error
                //we just end the method
            }
        }
    }




    /**
     * Called when a player is selecting how to pay for either a production or a development card.
     * Leverages the slot and resource closet methods.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void depositResourceIntoSlot(ClientDepositResourceIntoSlotMessage action, RemoteViewHandler view, User user){


        Player player = getModel().getPlayerFromUser(user);
//TODO
        if( !(player.getTurn())  || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            try{

            // Remove the specified resources from the specified source.
            selectResources(player, action.getResource(), action.getResourceId());
            // Select the slot relative to the action ( its production is being activated / a card is being placed in it)
            DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[action.getResourceId().getValue()];
            // Move the resources in the slot's resource closet to keep track of what is being paid.
            slot.setResourceCloset(action.getResource(), action.getResourceId());


            } catch (InvalidDepotException invalidDepotException) {
                //if i am trying to subtract more resources than the ones in a depot
                view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
            }
        }
    }

    //TODO: So this is called when one finishes putting together a single production? This parts looks a bit messy on the UX side? ALSO UI side might simplify checks.
    //TODO: might a single resource closet be more functional? DUE FOR REFACTORING.

    /**
     * Called when the player has finished selecting the resources needed to pay for one production.
     * Calls the check method to verify the resources deposited into the resource closet are enough and set the confirmed bool.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void confirmProduction(ClientConfirmProductionMessage action, RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn())  || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            try{

                //called if the player has deposit all the resources he need activate the production
                //and in this way he give a confirm that he wants to activate this production
                player.getDevelopmentCardSlots()[action.getSlotId().getValue()].check(false);
                //after this action is done the player can not undo his moves

            }catch (NotSufficientResourceException ex){
                try{
                    resetResources(player, ex.getTempResources());
                }catch(InvalidDepotException invalidDepotException){
                    //if i enter this catch there is a problem in the way i am restoring the resources
                    invalidDepotException.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when the user has selected and paid for all the production they wish to activate.
     * Toggles the main action from the player.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void activateProduction(RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn())  || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            player.toggleMainAction();
            DevelopmentCardSlot[] slots = player.getDevelopmentCardSlots();

            for (DevelopmentCardSlot dev : slots) {
                if (dev.isConfirmed()) {

                    player.getStrongbox().addResources(dev.activateProduction());
                }
            }
        }
    }


}

