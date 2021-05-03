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
            //and the player has been already infromed
        }
    }

    /**
     * Called when a player wants to deposit Resources from the tempResources in one of his depot
     * @param player
     * @param resource quantity that we want to remove from tempResouces
     */
    public void depositIntoWarehouse(Player player, Origin depot, Resource resource){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }

            Depot to = player.getWarehouse().get(depot.getValue());


            ArrayList<Resource> tempResources = player.getTempResources();

            ArrayList<Depot> warehouse = player.getWarehouse();
            Resource depot1 = player.getWarehouse().get(0).getResource();
            Resource depot2 = player.getWarehouse().get(1).getResource();
            Resource depot3 = player.getWarehouse().get(2).getResource();
/** DEVO FINIRE DI SCRIVERE LA LOGICA SUI CONTROLLI
 * una volta che ho la certezza di poter rimuovere le risorse da tempResources chiamerò
 * il metodo subFromTempResources(resource)
            if (depot == Origin.DEPOT_1 || depot == Origin.DEPOT_2 || depot == Origin.DEPOT_3) {
                if(depot == Origin.DEPOT_1) {
                    if(depot1.getResourceType() != null){
                        if(depot1.get)
                    }
                }else if(depot == Origin.DEPOT_2) {
                }else if(depot == Origin.DEPOT_3) {
                }else{
                    throw new RuntimeException("Ho fornito un Origin sbagliato");
            }else {
                //trying to deposit resource in a special depot
            }
**/
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }
    }

    /** DEVO FINIRE DI RIVEDERE QUESTI METODI PERO DOVEVO FARE IL COMMIT IN MODO CHE LE MODIFICHE
 * APPORTATE SIANO VISIBILI A TUTTI IN MODO DA NON IMPLEMENTARE CODICE NON NECESSARIO O GIA IMPLEMENTATO
    private void selectResources(Player player , Resource resource, Origin origin) throws AlreadyUsedActionException, IsNotYourTurnException, InvalidDepotException {
        if( !(player.getTurn()) ){
            throw new IsNotYourTurnException();
        }
        if( !(player.getMainAction()) ){
            throw new AlreadyUsedActionException();
        }

        if(origin != Origin.STRONGBOX){
            player.getWarehouse().get(origin.getValue()).subtractResource(resource);
        }else if(origin == Origin.STRONGBOX){
            player.getStrongbox().subResources(resource);
        }else {
            throw new RuntimeException("Ho fornito un origin scorretto")
        }

    }

    private void resetResources(Player player, HashMap<Origin, Resource> resourceHashMap) throws InvalidDepotException {
        Depot[] warehouse = player.getWarehouse();
        ArrayList<SpecialDepot> specialDepots = player.getSpecialDepots();

        for(Origin origin : resourceHashMap.keySet()) {
            switch (origin) {
                case DEPOT_1:
                    warehouse[0].addResource(resourceHashMap.get(origin));
                    break;
                case DEPOT_2:
                    warehouse[1].addResource(resourceHashMap.get(origin));
                    break;
                case DEPOT_3:
                    warehouse[2].addResource(resourceHashMap.get(origin));
                    break;
                case S_DEPOT_1:
                    specialDepots.get(0).addResource(resourceHashMap.get(origin));
                    break;
                case S_DEPOT_2:
                    specialDepots.get(1).addResource(resourceHashMap.get(origin));
                    break;
                case STRONGBOX:
                    player.getStrongbox().addResources(resourceHashMap.get(origin));
                default:
                    throw new RuntimeException("Problem while selecting production");

            }
        }
    }

    public void selectProduction(Player player, int slotId, Resource resource, Origin origin){
        try{
            selectResources(player, resource, origin);
            DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[slotId];
            slot.setTempResourcesProduction(resource, origin);
            //cosi facendo sposto le risorse sulla carta sviluppo o la boardProduction per confermare di avere posizionato
            //le risorse per quella carte bisognerà chiamare confirmProduction(player, slotid)
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        } catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
        }
    }

    public void confirmProduction(Player player, int slotId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            player.getDevelopmentCardSlots()[slotId].check();

        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(IS_NOT_YOUR_TURN);
            isNotYourTurnException.printStackTrace();
        } catch (AlreadyUsedActionException e) {
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(ALREADY_USED_ACTION);
            e.printStackTrace();
        }catch (NotSufficientResourceException notSufficientResourceException){
            try{
                resetResources(player, notSufficientResourceException.getTempResources());
            }catch(InvalidDepotException invalidDepotException){
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
                if(dev.isActivateProduction()){
                    player.getStrongbox().addResources(dev.activateProduction());
                }
            }
        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(IS_NOT_YOUR_TURN);
            isNotYourTurnException.printStackTrace();
        } catch (AlreadyUsedActionException e) {
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(ALREADY_USED_ACTION);
            e.printStackTrace();
        }
    }
**/
}