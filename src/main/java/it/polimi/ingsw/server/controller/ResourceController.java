package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.*;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class ResourceController{
    private static ResourceController singleton;
    private final Game model;

    private ResourceController(Game model){
        this.model = model;
    }

    public static ResourceController getInstance(Game model){
        if(singleton == null){
            singleton = new ResourceController(model);
        }

        return  singleton;

    }
    public static ResourceController destroy(){
        if(singleton != null){
            singleton = null;
        }
        return null;
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
    public void addFaithPoints(Player player, Resource faithPoints) throws EndOfGameException {
        try{
            FaithTrack faithTrack;
                if (player!= null) {
                    faithTrack = player.getFaithTrack();
                    faithTrack.addFaithPoints(faithPoints);
                    model.notify(new ServerFaithTrackMessage(
                            player.getVisibleFaithTrack(),
                            faithPoints.getQuantity(),
                            model.getUserFromPlayer(player)
                    ));
                } else {
                    faithTrack = model.getLorenzo().getFaithTrack();
                    faithTrack.addFaithPoints(faithPoints);
                    model.notify(new ServerFaithTrackMessage(
                            null,
                            faithPoints.getQuantity(),
                            null
                    ));
                }


        }catch (VaticanReportException vaticanReportException){

            // Run the validatePopeFavor for all the players in the game.
            ArrayList<Player> players = model.getPlayers();


            for(Player p : players){
                FaithTrack faithTrack = p.getFaithTrack();
                faithTrack.validatePopeFavor(vaticanReportException.getReport());

                if (p == player){
                    model.notify(new ServerFaithTrackMessage(
                            true,
                            player.getVisibleFaithTrack(),
                            faithPoints.getQuantity(),
                            model.getUserFromPlayer(p)
                    ));
                } else {
                    model.notify(new ServerFaithTrackMessage(
                            true,
                            p.getVisibleFaithTrack(),
                            0,
                            model.getUserFromPlayer(p)
                    ));
                }
            }

            if (model.isSolo()){
                model.getLorenzo().getFaithTrack().validatePopeFavor(vaticanReportException.getReport());
                model.notify(new ServerFaithTrackMessage(
                        true,
                        null,
                        faithPoints.getQuantity(),
                        null
                ));
            }

            if (vaticanReportException.getReport()==3) {
                throw new EndOfGameException(false);
            }

        }
    }


    /**
     * Subtracts resources from the strongbox or the warehouse as part of the process in which the user selects how to pay for a production/development card.
     * If the attempt to buy does not succeed, the resetResources method is invoked to place them back.
     * @param player Player whose resources are being managed.
     * @param idResourceMap a map with the source and the resource taken
     * @throws InvalidDepotException The selected Id does not contain the selected resource.
     */
    private void selectResources(Player player , Map<Id, Resource> idResourceMap) throws InvalidDepotException {


        ArrayList<Id> warehouseIds = new ArrayList<>(Arrays.asList( Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3, Id.S_DEPOT_1, Id.S_DEPOT_2));

        for(Id id : idResourceMap.keySet()){
            if(warehouseIds.contains(id)){
                player.getWarehouse().get(id.getValue()).subtractResource(idResourceMap.get(id));
            }else if(id == Id.STRONGBOX_SHIELD || id == Id.STRONGBOX_STONE || id == Id.STRONGBOX_SERVANT
                    || id == Id.STRONGBOX_COIN){
                player.getStrongbox().subResources(idResourceMap.get(id));
            }else {
                throw new RuntimeException("Supplied ResourceId doesn't belong to warehouse or strongbox.");
            }
        }

    }

    /**
     * Puts back the resources into strongbox/warehouse in case an attempt to buy something as failed.
     * @param player Player whose resources are being managed.
     * @param resourceHashMap Resources and the origin at which they must be returned.
     * @throws InvalidDepotException The previously supplied resources are somehow invalid.
     */
    public void resetResources(Player player, Map<Id, Resource> resourceHashMap) throws InvalidDepotException {
        List<Depot> warehouse = player.getWarehouse();

        ArrayList<Id> warehouseIds =  new ArrayList<>(Arrays.asList(Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3, Id.S_DEPOT_1, Id.S_DEPOT_2));

        for(Id id : resourceHashMap.keySet()){
            if(warehouseIds.contains(id)){
                warehouse.get(id.getValue()).addResource(resourceHashMap.get(id));
                resourceHashMap.remove(id);
            }else if(id == Id.STRONGBOX_SHIELD || id == Id.STRONGBOX_STONE || id == Id.STRONGBOX_SERVANT
                    || id == Id.STRONGBOX_COIN){
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
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void throwResources(RemoteViewHandler view, User user) throws EndOfGameException {
        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            int thrown = 0;
            List<Resource> tempResources = player.getTempResources();
            Resource faithPoints;

            if (tempResources != null) {


                for (Resource resource : tempResources) {
                    thrown += resource.getQuantity();
                }

                player.dumpTempResources();
                model.notify(new ServerThrowResourceMessage(
                        thrown,
                        model.getUserFromPlayer(player)
                ));

                faithPoints = new Resource(thrown, ResourceType.FAITH);

                if (!model.isSolo()) {
                    ArrayList<Player> players = model.getPlayers();
                    players.remove(player);

                    for (Player p : players) {
                        addFaithPoints(p, faithPoints);
                    }
                } else {
                    // Add them to Lorenzo.
                    addFaithPoints(null,faithPoints);
                }
            }
        }
    }

    /**
     * Called when a player want to withdraw Resources from a depot in order.
     * to put them in an other depot or if he want to throw them.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void tidyWarehouse(ClientTidyWarehouseMessage action, RemoteViewHandler view, User user){
        Player player = model.getPlayerFromUser(user);

        // A player can rearrange their warehouse even if not their turn
        if( model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {
            try {
                Depot fromDepot = player.getWarehouse().get(action.getFrom().getValue());
                Depot toDepot = player.getWarehouse().get(action.getTo().getValue());


                Resource fromResource = fromDepot.getResource();
                Resource toResource = toDepot.getResource();

                // If both deposits are empty, there is nothing to do
                if ((toResource == null || toResource.getQuantity() == 0)&&(fromResource== null || fromResource.getQuantity() == 0)) {

                    view.handleStatusMessage(StatusMessage.CONTINUE);
                    return;

                }  else if (toResource == null || toResource.getQuantity() == 0) {
                    //if i am trying to move resources to an empty specialDepot or a empty normal depot
                    toDepot.addResource(fromResource);
                    //if no exception is thrown
                    fromDepot.subtractResource(fromResource);

                } else if (fromResource == null || fromResource.getQuantity() == 0) {
                    //if i am trying to move resources from an empty specialDepot or a empty normal depot
                    fromDepot.addResource(toResource);
                    //if no exception is thrown
                    toDepot.subtractResource(toResource);

                } else {

                    if (fromResource.getQuantity() <= toDepot.getCapacity() && toResource.getQuantity() <= fromDepot.getCapacity()) {
                        //i can switch the content of the depot
                        toDepot.subtractResource(toResource);
                        fromDepot.subtractResource(fromResource);

                        toDepot.addResource(fromResource);
                        fromDepot.addResource(toResource);

                    } else {
                        view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
                    }
                }

                //every time a change is done in the warehouse a copy of it is sent to every player
                model.notify(new ServerWarehouseMessage(
                        player.getVisibleWarehouse(),
                        model.getUserFromPlayer(player)
                ));


            } catch (InvalidDepotException invalidDepotException) {
                //if i enter here it means that the action on the warehouse has failed
            view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            }
        }
    }

    /**
     * Called when a player is depositing Resources from the tempResources acquired from the market in one of his depots.
     * Removes the selected resource from tempResources.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void depositIntoWarehouse(ClientDepositIntoWarehouseMessage action, RemoteViewHandler view, User user){

        Player player = model.getPlayerFromUser(user);

        if(     !(player.getTurn()) ||
                model.getGameState() != GameState.PLAY ||
                //The request was malformed and references a resource not contained in tempResources.
                !player.tempResourcesContains(action.getResource()) ) {

            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);

        } else {


            try {
                List<Depot> warehouse = player.getWarehouse();
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
                player.subFromTempResources(action.getResource());
                model.notify(new ServerDepositActionMessage(
                        player.getTempResources(),
                        player.getVisibleWarehouse(),
                        action.getResource(),
                        model.getUserFromPlayer(player)
                ));


            } catch (InvalidDepotException invalidDepotException) {
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            }
        }
    }


    /**
     * Called when a player is selecting how to pay for either a production or a development card.
     * Leverages the slot and resource closet methods.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void depositResourceIntoSlot(ClientDepositResourceIntoSlotMessage action, RemoteViewHandler view, User user){

        Player player = model.getPlayerFromUser(user);
        if( !(player.getTurn())  || !(player.getMainAction()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            try{

                // Remove the specified resources from the specified source.
                selectResources(player, action.getIdResourceMap());
                // Select the slot relative to the action ( its production is being activated / a card is being placed in it)
                Slot slot = player.getSlots().get(action.getSlotId().getValue());
                // Move the resources in the slot's resource closet to keep track of what is being paid.
                slot.setResourceCloset(action.getIdResourceMap());

                /*
                 * Called when the player has finished selecting the resources needed to pay for one production.
                 * Calls the check method to verify the resources deposited into the resource closet are enough and set the confirmed bool.
                 */
                slot.check(action.isForCard());

                //if the player has chosen a production that return a jolly we set the output resourceType
                if(slot instanceof BoardProduction){
                    ((BoardProduction)slot).chooseJolly(action.getJollyType());
                }else if(slot instanceof SpecialProduction){
                    ((SpecialProduction)slot).chooseJolly(action.getJollyType());
                }

                //if no exception is thrown this production or card is ready to be activated or bought
                model.notify(new ServerDepositIntoSlotMessage(
                        player.getVisibleWarehouse(),
                        player.getVisibleStrongbox(),
                        player.getVisibleSlots(),
                        model.getUserFromPlayer(player)

                ));

            } catch (InvalidDepotException invalidDepotException) {
                //if i am trying to subtract more resources than the ones in a depot
                //this should never happen
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            } catch (NotSufficientResourceException e) {
                //if the check fails
                try{
                    resetResources(player, e.getTempResources());
                    view.handleStatusMessage(StatusMessage.SELECTION_ERROR);
                }catch(InvalidDepotException invalidDepotException){
                    //if i enter this catch there is a problem in the way i am restoring the resources
                    invalidDepotException.printStackTrace();
                    view.handleStatusMessage(StatusMessage.SERVER_ERROR);
                }

            }
        }
    }


    /**
     * Called when the user has selected and paid for all the production they wish to activate.
     * Toggles the main action from the player.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void activateProduction(ClientActivateProductionMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn())  || !(player.getMainAction()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {
            player.toggleMainAction();
            List<Slot> slots = player.getSlots();
            HashMap<ResourceType, Resource> spent = new HashMap<>();
            HashMap<ResourceType, Resource> gained = new HashMap<>();

            for (Slot dev : slots) {
                if (dev.isConfirmed()) {

                    for (Resource resource: dev.productionCost()){
                        if (spent.containsKey(resource.getResourceType())) {
                            spent.get(resource.getResourceType()).add(resource);
                        } else spent.put(resource.getResourceType(), resource);
                    }

                    Resource[] productionOut = dev.activateProduction();

                    for (Resource resource: productionOut){
                        if (gained.containsKey(resource.getResourceType())) {
                            gained.get(resource.getResourceType()).add(resource);
                        } else gained.put(resource.getResourceType(), resource);

                        if (resource.getResourceType()== ResourceType.FAITH){
                            addFaithPoints(player, resource);
                        } else {
                            player.getStrongbox().addResources(resource);
                        }
                    }



                }
            }


            model.notify(new ServerActivateProductionMessage(
                    player.getVisibleStrongbox(),
                    model.getUserFromPlayer(player),
                    new ArrayList<Resource>(gained.values()), new ArrayList<Resource>(spent.values())));

        }
    }


}
