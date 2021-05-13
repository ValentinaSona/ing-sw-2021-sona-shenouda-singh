package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.DevelopmentCardSlot;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyTargetCardMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSelectDevelopmentCardMessage;


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

    /** TODO: added preliminary check that the player does have resources to buy the card. Check that it fits with discountAbility implementation.
     * Called when te player selects a card they wish to buy. Checks whether the card and the slot selected are valid before proceeding to the payment.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void selectDevelopmentCard(ClientSelectDevelopmentCardMessage action, RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            DevelopmentCard targetCard = developmentCardsMarket.getDevelopmentCards(action.getRow(),action.getCol());
            DevelopmentCardSlot target = player.getDevelopmentCardSlots()[action.getTargetSlot().getValue()-1];


            try {
                // Check if player has enough resources at ALL. Assumes the card is presented with it's cost lowered if discounted.
                if (!player.canPay(targetCard.getCost())){throw new NotSufficientResourceException();}

                target.setTargetCard(targetCard, action.getRow(), action.getCol());
                view.handleStatusMessage(StatusMessage.CONTINUE);

            } catch (DevelopmentCardException | NotSufficientResourceException e) {
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            }
        }
    }

    /** TODO: this needs some serious refactoring. Why are we having slot.check() set a boolean? It should not get to calling buy slot.buyDevelopmentCard() anyway if it throws?
     * Called to actually buy the card and
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void buyTargetCard(ClientBuyTargetCardMessage action, RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            try {

                DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[action.getSlotId().getValue() - 1];
                slot.check(true);
                //if check doesn't throw any exception i can proceed to buy the card
                //in this case i don't have to separate the two action as opposed to the production process of validation because i can buy only one card every turn
                player.toggleMainAction();
                slot.buyDevelopmentCard();
                //now i can remove the card from the market
                //TODO: is the return value actually ever used? Also why plural?
                developmentCardsMarket.buyDevelopmentCards(slot.getRow(), slot.getCol());
            } catch (NotSufficientResourceException e) {
                try {
                    resourceController.resetResources(player, e.getTempResources());
                } catch (InvalidDepotException invalidDepotException) {
                    //if i enter this catch there is a problem in the way i am restoring the resources
                    invalidDepotException.printStackTrace();
                }
            }
        }
    }
}
