package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.AlreadyUsedActionException;
import it.polimi.ingsw.exception.IsNotYourTurnException;
import it.polimi.ingsw.exception.NotSufficientResourceException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentCardMarketController extends AbstractController{

    private final DevelopmentCardsMarket developmentCardsMarket;

    private final ResourceController resourceController;

    private static DevelopmentCardMarketController singleton;


    private DevelopmentCardMarketController(){
        this.developmentCardsMarket = DevelopmentBuilder.build();
        this.resourceController = ResourceController.getInstance();
    }

    public static DevelopmentCardMarketController getInstance(){
        if(singleton == null){
            singleton = new DevelopmentCardMarketController();
        }

        return  singleton;

    }



    public void selectDevelopmentCard(Player player, int row, int col, int targetSlot){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            //controllo che tale carta sia posizionabile in uno degli slot disponibili
            //nel momento in cui ciò è vero gli chiedo di scegliere le risorse

            //DevelopmentCardSlot slot = player.getDevelopmentSlot(slotId);
            //slot.setTempResourcesProduction(origin, resource);
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

    public void selectResources(Player player, Resource resource, Origin origin){}

    public void confirmChoice(Player player){
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
                        player.getStrongbox().addResources(resourceHashMap.get(origin));
                    default:
                        throw new RuntimeException("Problem while selecting production");
                }
            }
        }
    }


    /*  Called when the player selects a development card.
        Checks that the player has not yet made his main action this turn and that they meet the prerequisites for buying that card.
        If so, saves the card temporarily in player.tryToBuy.
     */
    public void getDevelopmentCard(Player player, int row, int col) {

    }
    /* Called when the player selects which resources to use to buy the card.
       Calls player.getAvailableResources() to checkProduction they possess the resources selected, then withdraws the correct amounts from each location.
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