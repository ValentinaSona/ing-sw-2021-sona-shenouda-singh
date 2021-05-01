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
            //devo creare tre metodi che gesticano i 3 casi delle pope favor tile
            //ossia prende la posizione di ogni giocatore e vede se gli deve girare la popeFavorTile
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
            Resource[] tempResources = player.getTempResources();
            Resource faithPoints;


            for(Resource resource : tempResources) {
                thrown += resource.getQuantity();
            }

            faithPoints = new Resource(thrown, ResourceType.FAITH);

            ArrayList<Player> players = getPlayersList();
            players.remove(player);

            for(Player p :  players) {

                addFaithPoints(p, faithPoints);

            }


        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(IS_NOT_YOUR_TURN);
            isNotYourTurnException.printStackTrace();
        }
    }

    /**
     * Called when a player want to withdraw Resources from a depot in order
     * to put them in an other depot or if he want to throw them
     * @param player
     * @param fromDepot
     */
    public void withdrawFromDepot(Player player, int fromDepot, boolean specialDepot){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( fromDepot< 1 || fromDepot > 3)
                throw  new RuntimeException("Errore con i numeri dei depositi");
            Depot from;

            if(specialDepot){
                from = player.getSpcialDepots().get(fromDepot);
            }else{
                Depot[] warehouse = player.getWarehouse();
                from = warehouse[fromDepot];
            }

            ArrayList<Resource> tempResources = player.getTempResources();

            for(Resource res : tempResources){
                if((from.getResource() != null) && (res.getResourceType() == from.getResource().getResourceType())){
                    res.add(from.withdrawResource());
                    break;
                }
            }

            //if in temp resources there was not a resource of the same type of
            // the one in the depot
            if( from.getResource() != null){
                tempResources.add(from.withdrawResource());
            }

        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(IS_NOT_YOUR_TURN);
            isNotYourTurnException.printStackTrace();
        }catch (InvalidDepotException invalidDepotException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(invalidDepotException.getMessage());
            invalidDepotException.printStackTrace();
        }
    }

    /**
     * Called when a player wants to deposit Resources from the tempResources in one of his depot
     * @param player
     * @param toDepot targetDepot
     * @param resource quantity that we want to remove from tempResouces
     */
    public void depositIntoDepot(Player player, int toDepot, Resource resource, boolean specialDepot){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( toDepot< 1 || toDepot > 3)
                throw  new RuntimeException("Error about the index of the depot");

            Depot to;

            if(specialDepot){
                to = player.getSpcialDepots().get(toDepot);
            }else{
                Depot[] warehouse = player.getWarehouse();
                to = warehouse[toDepot];
            }
            ArrayList<Resource> tempResources = player.getTempResources();

            for(Resource res : tempResources){
                if(res.getResourceType() == resource.getResourceType()){
                    if(res.getQuantity() == resource.getQuantity()){
                        to.addResource(resource);
                        //if the operation complete without throwing an exception
                        //then i can remove the resource from the tempResource otherwise the player
                        //has to do a new action
                        tempResources.remove((Resource) res);
                    }else{
                        to.addResource(resource);
                        res.add(new Resource(-resource.getQuantity(),resource.getResourceType() ));
                    }
                    break;
                }
            }
        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(IS_NOT_YOUR_TURN);
            isNotYourTurnException.printStackTrace();
        }catch (InvalidDepotException invalidDepotException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn or i can alert that player
            //by throwing an update player.throwError(invalidDepotException.getMessage());
            invalidDepotException.printStackTrace();
        }
    }

    public void selectProduction(Player player, int slotId, Resource resource, Origin origin){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            Depot[] warehouse = player.getWarehouse();
            ArrayList<SpecialDepot> specialDepots = player.getSpecialDepots();
            /**
             * E palesemente più comodo creare un metodo che faccia la sottrazione
             */
            switch (origin){
                case DEPOT_1:
                    warehouse[0].addResource(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                case DEPOT_2:
                    warehouse[1].addResource(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                case DEPOT_3:
                    warehouse[2].addResource(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                case S_DEPOT_1:
                    specialDepots.get(0).addResource(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                case S_DEPOT_2:
                    specialDepots.get(1).addResource(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                case STRONGBOX:
                    player.getStrongbox().addResources(new Resource(-resource.getQuantity(), resource.getResourceType()));
                    break;
                default:
                    throw new RuntimeException("Problem while selecting production");
            }
            //DevelopmentCardSlot slot = player.getDevelopmentSlot(slotId);
            //slot.setTempResources(origin, resource);
            //cosi facendo sposto le risorse sulla carta sviluppo o la boardProduction per confermare di avere posizionato
            //le risorse per quella carte bisognerà chiamare confirmProduction(player, slotid)
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

    public void confirmProduction(Player player, int slotId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            player.getDevelopmentSlot(slotId).check();

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
            HashMap<Origin, Resource> resourceHashMap = notSufficientResourceException.getTempResources();
            Depot[] warehouse = player.getWarehouse();
            ArrayList<SpecialDepot> specialDepots = player.getSpecialDepots();

            for(Origin origin : resourceHashMap.keySet()){
                switch (origin){
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
                        player.getStrongbox().addResource(resourceHashMap.get(origin));
                    default:
                        throw new RuntimeException("Problem while selecting production");
                }
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

            //chiama il metodo activate production di tutti i developmentSlot con flag attivo
            //e mette le produzioni nella strongBox
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

}