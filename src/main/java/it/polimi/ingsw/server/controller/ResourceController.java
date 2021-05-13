package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.Depot;
import it.polimi.ingsw.server.model.observable.DevelopmentCardSlot;
import it.polimi.ingsw.server.model.observable.FaithTrack;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientDepositIntoWarehouseMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientTidyWarehouseMessage;

import java.util.ArrayList;
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


    /*
     */

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

        }


        try{
            ArrayList<Depot> warehouse = player.getWarehouse();
            Depot targetDepot = warehouse.get(action.getSlotId().getValue());

            Id[] standardDepots = {Id.DEPOT_1, Id.DEPOT_2,Id.DEPOT_3};

            // If the deposit is not empty and it contains a resource of a different type than the one we are trying to deposit the action is illegal. Redundant check.
            if (targetDepot.getResource() != null && targetDepot.getResource().getResourceType() != action.getResource().getResourceType()){throw new InvalidDepotException();}

            for (Id depotId : standardDepots){
                if (depotId != action.getSlotId()){
                    if ( warehouse.get(depotId.getValue()).getResource() != null && warehouse.get(depotId.getValue()).getResource().getResourceType() == action.getResource().getResourceType()){

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


          /*  Resource depot1Resource = player.getWarehouse().get(Id.DEPOT_1.getValue()).getResource();
            Resource depot2Resource = player.getWarehouse().get(Id.DEPOT_2.getValue()).getResource();
            Resource depot3Resource = player.getWarehouse().get(Id.DEPOT_3.getValue()).getResource();

            if(action.getSlotId() == Id.DEPOT_1){
                checkDepotType(targetDepot,depot2Resource,depot3Resource, resource);
            }else if(target == Id.DEPOT_2){
                checkDepotType(targetDepot,depot1Resource,depot3Resource, resource);
            }else if(target == Id.DEPOT_3){
                checkDepotType(targetDepot,depot1Resource,depot2Resource, resource);
            }else{
                //the target depot is a special depot we don't have to do any check
                targetDepot.addResource(resource);
                //if the operation succeeded we will remove the resource from tempResources
                player.subFromTempResources(resource);
            } */

        }catch (InvalidDepotException invalidDepotException){
            //the depot model has already notified the player of the error
            //we just end the method
        }
    }

 /*   private void checkDepotType(Depot targetDepot, Resource depotA, Resource depotB, Resource resource) throws InvalidDepotException{
        if( depotA != null && depotB != null){
            if(resource.getResourceType() != depotA.getResourceType() &&
                    resource.getResourceType() != depotB.getResourceType()){
                //i can try to add this resourceType to the target depot
                targetDepot.addResource(resource);
                //if the action fails the InvalidDepotException is thrown and the next statement is
                //never executed
                getCurrentPlayer().subFromTempResources(resource);
            }else{
                getCurrentPlayer().throwError(AbstractModel.WAREHOUSE_TYPE_ERROR);
            }
        }else if(depotA == null && depotB != null){
            if(depotB.getResourceType() != resource.getResourceType()){
                //i can try to add this resourceType to the target depot
                targetDepot.addResource(resource);
                //if the action fails the InvalidDepotExceptin is thrown and the next statement is
                //never executed
                getCurrentPlayer().subFromTempResources(resource);
            }else{
                getCurrentPlayer().throwError(AbstractModel.WAREHOUSE_TYPE_ERROR);
            }
        }else if(depotB == null && depotA != null){
            if(depotA.getResourceType() != resource.getResourceType()){
                //i can try to add this resourceType to the target depot
                targetDepot.addResource(resource);
                //if the action fails the InvalidDepotExceptin is thrown and the next statement is
                //never executed
                getCurrentPlayer().subFromTempResources(resource);
            }else{
                getCurrentPlayer().throwError(AbstractModel.WAREHOUSE_TYPE_ERROR);
            }

        }else{
            //both the depots are null no problem
            targetDepot.addResource(resource);
            //if the action fails the InvalidDepotExceptin is thrown and the next statement is
            //never executed
            getCurrentPlayer().subFromTempResources(resource);
        }
    } */

    private void selectResources(Player player , Resource resource, Id resourceId) throws InvalidDepotException {

        if(resourceId != Id.STRONGBOX){
            player.getWarehouse().get(resourceId.getValue()).subtractResource(resource);
        }else if(resourceId == Id.STRONGBOX){
            player.getStrongbox().subResources(resource);
        }else {
            throw new RuntimeException("Ho fornito un origin scorretto");
        }

    }

    public void resetResources(Player player, HashMap<Id, Resource> resourceHashMap) throws InvalidDepotException {
        ArrayList<Depot> warehouse = player.getWarehouse();

        for(Id id : resourceHashMap.keySet()){
            if(id != Id.STRONGBOX){
                warehouse.get(id.getValue()).addResource(resourceHashMap.get(id));
                resourceHashMap.remove(id);
            }else if(id == Id.STRONGBOX){
                player.getStrongbox().addResources(resourceHashMap.get(id));
                resourceHashMap.remove(id);
            }else {
                throw new RuntimeException("Ho fornito un id scorretto");
            }
        }
    }

    public void depositResourceIntoSlot(Player player, Id slotId, Resource resource, Id resourceId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            selectResources(player, resource, resourceId);
            DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[slotId.getValue()];
            slot.setResourceCloset(resource, resourceId);
        }catch (IsNotYourTurnException isNotYourTurnException){
           // player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
          //  player.throwError(AbstractModel.ACTION_USED);
        }catch (InvalidDepotException invalidDepotException){
            //if i am trying to subtract more resources than the ones in a depot

        }
    }

    public void confirmProduction(Player player, Id slotId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            //called if the player has deposit all the resources he need activate the production
            //and in this way he give a confirm that he wants to activate this production
            player.getDevelopmentCardSlots()[slotId.getValue()].check(false);
            //after this action is done the player can not undo his moves
        }catch (IsNotYourTurnException isNotYourTurnException){
          //  player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
         //   player.throwError(AbstractModel.ACTION_USED);
        }catch (NotSufficientResourceException ex){
            try{
                resetResources(player, ex.getTempResources());
            }catch(InvalidDepotException invalidDepotException){
                //if i enter this catch there is a problem in the way i am restoring the resources
                invalidDepotException.printStackTrace();
            }
        }
    }

    public void activateProduction(Player player){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            player.toggleMainAction();
            DevelopmentCardSlot[] slots = player.getDevelopmentCardSlots();

            for(DevelopmentCardSlot dev : slots){
                if(dev.isConfirmed()){

                    player.getStrongbox().addResources(dev.activateProduction());
                }
            }
        }catch (IsNotYourTurnException isNotYourTurnException){
          //  player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
          //  player.throwError(AbstractModel.ACTION_USED);
        }
    }

}

