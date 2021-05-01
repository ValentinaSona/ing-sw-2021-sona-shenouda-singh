package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.AlreadyUsedActionException;
import it.polimi.ingsw.exception.IsNotYourTurnException;
import it.polimi.ingsw.exception.TwoLeaderCardsException;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;

public class MarketController extends AbstractController {
    private static MarketController singleton;
    private final Market market;
    private final ResourceController resourceController;

    private MarketController(){
        this.market = MarketBuilder.build();
        this.resourceController = ResourceController.getInstance();
    }

    public static MarketController getInstance(){
        if(singleton == null){
            singleton = new MarketController();
        }

        return  singleton;

    }

    public void buyMarbles(Player player, int rowcol) {
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }
            MarketMarble[] marbles = market.getResources(player,rowcol);

            convertMarbles(marbles);
        }catch(TwoLeaderCardsException twoLeaderCardsException){
            //if the currentPlayer does not posses 2 LeaderCards the marbles are
            //returned without throwing any exception otherwise an exception is thrown
            //and the currentPlayer is notified that he has to choose how to convert the white
            //marbles using his two leaderCards
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

    /*  Called when the player has been prompted to choose which resources he wishes to convert his white marbles in as result of twoLeaderCardsException.
        Takes as an input an array of MarketMarbles that it asks the market to substitute to the white marbles in the previous array.
        Passes the new array to convertMarbles, returning to the normal control flow.
     */

    public void convertWhiteMarbles(Player player, MarketMarble[] choices) {
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            MarketMarble[] marbles = market.getChosen(choices);
            convertMarbles(marbles);
        }catch (IsNotYourTurnException isNotYourTurnException){
            //I should not enter here if we do enter here we have a problem in the gui code
            //that shouldn't enable any button if is not you turn
            isNotYourTurnException.printStackTrace();
        }
    }

    /*  Called on the definitive array of gained market marbles.
        Returns an array of market marbles into an array of resources.
        If faith has been collected, it issues a call to the ResourcesController to add the faith to the user's faith track.
        Saves the resources array (without faith in it) to player.tempResources.
     */
    private void convertMarbles(MarketMarble[] marbles) {
        ArrayList<Resource> temp = new ArrayList<>();
        Resource faithPoints = null;

        for(ResourceType type : ResourceType.values()){
            int count = 0;
            for(MarketMarble marble : marbles){
                if(marble.convertToResource() == type)
                    count++;
            }
            if(count != 0)
                temp.add(new Resource(count, type));
        }
        //controlling if there is a faith resource
        for(Resource res : temp){
            if(res.getResourceType() == ResourceType.FAITH){
                faithPoints = res;
                temp.remove(res);
                break;
            }
        }

        //if faithPoints is different from null
        if(faithPoints != null){
            resourceController.addFaithPoints(getCurrentPlayer(), faithPoints);
        }

        Resource[] tempResources =(Resource[]) temp.toArray();
        getCurrentPlayer().setTempResources(tempResources);

    }
}