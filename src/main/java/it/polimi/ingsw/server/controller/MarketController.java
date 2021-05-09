package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.AlreadyUsedActionException;
import it.polimi.ingsw.server.exception.IsNotYourTurnException;
import it.polimi.ingsw.server.exception.TwoLeaderCardsException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.server.model.*;

import java.util.ArrayList;

public class MarketController extends AbstractController {
    private static MarketController singleton;
    private final Market market;
    private final ResourceController resourceController;

    private MarketController(Model model){
        super(model);
        this.market = MarketBuilder.build();
        this.resourceController = ResourceController.getInstance(model);
    }

    public static MarketController getInstance(Model model){
        if(singleton == null){
            singleton = new MarketController(model);
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

            player.toggleMainAction();
            MarketMarble[] marbles = market.getResources(player,rowcol);

            convertMarbles(marbles);
        }catch(TwoLeaderCardsException twoLeaderCardsException){
            //if the currentPlayer does not posses 2 LeaderCards the marbles are
            //returned without throwing any exception otherwise an exception is thrown
            //and the currentPlayer is notified that he has to choose how to convert the white
            //marbles using his two leaderCards
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        } catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
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
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
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
        //control if there is a faith resource
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

        getCurrentPlayer().addToTempResources(temp);

    }

    /**
     * This method is called by the LeaderCardsController when the condition to activate
     * a leaderCard are verified
     * @param player
     * @param marble
     */
    public void addMarketAbility(Player player, MarketMarble marble){
        market.addAbility(marble, player);
    }
}