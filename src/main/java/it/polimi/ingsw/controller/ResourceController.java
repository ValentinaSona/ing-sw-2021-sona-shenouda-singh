package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ResourceController extends AbstractController {
    private static ResourceController singleton;


    private ResourceController(){
    }

    public static ResourceController getInstance(){
        if(singleton == null){
            singleton = new ResourceController();
        }

        return  singleton;

    }


    /* Public method called by this and other controllers when they need to add faith points to a player's faith track.
       Calls the faith track method with the same name and handles the VaticanReport exception by calling the vaticanReport method.
       This method doesn't do any control on the caller because it is only called by other methods of the controllers that have already done this type of checks.
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


    /*  Called when the player ends processing the resources gained from the market.
        Counts the wasted resources, sets tempResources to null and adds faith points to other players if needed.
     */
    public void throwResources(Player player) {

        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }

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
        }catch (IsNotYourTurnException isNotYourTurnException){
            //notify the player
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }
    }

    /**
     * Called when a player want to withdraw Resources from a depot in order
     * to put them in an other depot or if he want to throw them
     * @param player
     */

    public void tidyWarehouse(Player player,Origin from, Origin to){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }

            Depot fromDepot = player.getWarehouse().get(from.getValue());
            Depot toDepot = player.getWarehouse().get(to.getValue());


            Resource fromResource = fromDepot.getResource();
            Resource toResource = toDepot.getResource();


            if(toDepot.getResource() == null || toDepot.getResource().getQuantity() == 0){
                //if i am trying to move resources to an empty specialDepot or a empty normal depot
                toDepot.addResource(fromResource);
                //if no exception is thrown
                fromDepot.subtractResource(toResource);
            }else{
                if(fromResource.getQuantity() <= toDepot.getCapacity() && toResource.getQuantity() <= fromDepot.getCapacity()) {
                    //i can switch the content of the depot
                    toDepot.subtractResource(toResource);
                    fromDepot.subtractResource(fromResource);

                    toDepot.addResource(fromResource);
                    fromDepot.addResource(toResource);
                }else {
                    player.throwError(AbstractModel.ILLEGAL_ACTION);
                }
            }

        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (InvalidDepotException invalidDepotException){
            //if i enter here it means that the action on the warehouse is failed
            //and the player has been already infrormed
        }
    }

    /**
     * Called when a player wants to deposit Resources from the tempResources in one of his depot
     * @param player
     * @param resource quantity that we want to remove from tempResouces
     */
    public void depositIntoWarehouse(Player player, Origin target, Resource resource){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }

            Depot targetDepot = player.getWarehouse().get(target.getValue());

            ArrayList<Depot> warehouse = player.getWarehouse();

            Resource depot1Resource = player.getWarehouse().get(Origin.DEPOT_1.getValue()).getResource();
            Resource depot2Resource = player.getWarehouse().get(Origin.DEPOT_2.getValue()).getResource();
            Resource depot3Resource = player.getWarehouse().get(Origin.DEPOT_3.getValue()).getResource();

            if(target == Origin.DEPOT_1){
                checkDepotType(targetDepot,depot2Resource,depot3Resource, resource);
            }else if(target == Origin.DEPOT_2){
                checkDepotType(targetDepot,depot1Resource,depot3Resource, resource);
            }else if(target == Origin.DEPOT_3){
                checkDepotType(targetDepot,depot1Resource,depot2Resource, resource);
            }else{
                //the target depot is a special depot we don't have to do any check
                targetDepot.addResource(resource);
                //if the operation succed succesfuly we will remove the resource from tempResources
                player.subFromTempResources(resource);
            }
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (InvalidDepotException invalidDepotException){
            //the depot model has already notified the player of the error
            //we just end the method
        }
    }

    private void checkDepotType(Depot targetDepot, Resource depotA, Resource depotB, Resource resource) throws InvalidDepotException{
        if( depotA != null && depotB != null){
            if(resource.getResourceType() != depotA.getResourceType() &&
                    resource.getResourceType() != depotB.getResourceType()){
                //i can try to add this resourceType to the target depot
                targetDepot.addResource(resource);
                //if the action fails the InvalidDepotExceptin is thrown and the next statement is
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
    }

    private void selectResources(Player player , Resource resource, Origin resourceOrigin) throws InvalidDepotException {

        if(resourceOrigin != Origin.STRONGBOX){
            player.getWarehouse().get(resourceOrigin.getValue()).subtractResource(resource);
        }else if(resourceOrigin == Origin.STRONGBOX){
            player.getStrongbox().subResources(resource);
        }else {
            throw new RuntimeException("Ho fornito un origin scorretto");
        }

    }

    private void resetResources(Player player, HashMap<Origin, Resource> resourceHashMap) throws InvalidDepotException {
        ArrayList<Depot> warehouse = player.getWarehouse();

        for(Origin origin : resourceHashMap.keySet()){
            if(origin != Origin.STRONGBOX){
                warehouse.get(origin.getValue()).addResource(resourceHashMap.get(origin));
                resourceHashMap.remove(origin);
            }else if(origin == Origin.STRONGBOX){
                player.getStrongbox().addResources(resourceHashMap.get(origin));
                resourceHashMap.remove(origin);
            }else {
                throw new RuntimeException("Ho fornito un origin scorretto");
            }
        }
    }

    public void selectProduction(Player player, Origin slotId, Resource resource, Origin resourceOrigin){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            selectResources(player, resource, resourceOrigin);
            DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[slotId.getValue()];
            slot.setResourceCloset(resource, resourceOrigin);
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
        }catch (InvalidDepotException ex){

        }
    }

    public void confirmProduction(Player player, Origin slotId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            //called if the player has deposit all the resources he need activate the production
            //and in this way he give a confirm that he wants to activate this production
            player.getDevelopmentCardSlots()[slotId.getValue()].check();

        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
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

            DevelopmentCardSlot[] slots = player.getDevelopmentCardSlots();

            for(DevelopmentCardSlot dev : slots){
                if(dev.isConfirmed()){
                    player.getStrongbox().addResources(dev.activateProduction());
                }
            }
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
        }
    }

}

