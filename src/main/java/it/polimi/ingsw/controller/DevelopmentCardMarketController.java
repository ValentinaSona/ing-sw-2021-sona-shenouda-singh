package it.polimi.ingsw.controller;

import it.polimi.ingsw.exception.*;
import it.polimi.ingsw.model.*;

public class DevelopmentCardMarketController extends AbstractController{

    private final DevelopmentCardsMarket developmentCardsMarket;

    private final ResourceController resourceController;

    private static DevelopmentCardMarketController singleton;


    private DevelopmentCardMarketController(Model model){
        super(model);
        this.developmentCardsMarket = DevelopmentBuilder.build();
        this.resourceController = ResourceController.getInstance(model);
    }

    public static DevelopmentCardMarketController getInstance(Model model){
        if(singleton == null){
            singleton = new DevelopmentCardMarketController(model);
        }

        return  singleton;

    }

    public void selectDevelopmentCard(Player player, int row, int col, Id targetSlot){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }
            DevelopmentCard targetCard = developmentCardsMarket.getDevelopmentCards(row,col);
            DevelopmentCardSlot target = player.getDevelopmentCardSlots()[targetSlot.getValue()-1];
            target.setTargetCard(targetCard, row, col);

        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException alreadyUsedActionException) {
            player.throwError(AbstractModel.ACTION_USED);
        }catch (DevelopmentCardException developmentCardException) {
            //the model will inform the player of the invalid action
        }
    }

    public void buyTargetCard(Player player, Id slotId){
        try{
            if( !(player.getTurn()) ){
                throw new IsNotYourTurnException();
            }
            if( !(player.getMainAction()) ){
                throw new AlreadyUsedActionException();
            }

            DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[slotId.getValue()-1];
            slot.check(true);
            //if check doesn't throw any exception i can procede and buy the card
            //in this case i don't have to separate the two action as opposed to the production process of validation because i can buy only one card every turn
            player.toggleMainAction();
            slot.buyDevelopmentCard();
            //now i can remove the card from the market
            developmentCardsMarket.buyDevelopmentCards(slot.getRow(), slot.getCol());
        }catch (IsNotYourTurnException isNotYourTurnException){
            player.throwError(AbstractModel.IS_NOT_YOUR_TURN);
        }catch (AlreadyUsedActionException e) {
            player.throwError(AbstractModel.ACTION_USED);
        }catch (NotSufficientResourceException ex){
            try{
                resourceController.resetResources(player, ex.getTempResources());
            }catch(InvalidDepotException invalidDepotException){
                //if i enter this catch there is a problem in the way i am restoring the resources
                invalidDepotException.printStackTrace();
            }
        }
    }
}
