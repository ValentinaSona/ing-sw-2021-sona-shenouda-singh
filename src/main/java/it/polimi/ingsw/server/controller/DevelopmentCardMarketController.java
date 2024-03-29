package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.*;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyTargetCardMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSelectDevelopmentCardMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerBuyDevelopmentCardMessage;


public class DevelopmentCardMarketController{

    private final ResourceController resourceController;
    private Game model;
    private static DevelopmentCardMarketController singleton;


    private DevelopmentCardMarketController(Game model){
        this.model = model;
        this.resourceController = ResourceController.getInstance(model);
    }

    public static DevelopmentCardMarketController getInstance(Game model){
        if(singleton == null){
            singleton = new DevelopmentCardMarketController(model);
        }

        return  singleton;

    }

    public static DevelopmentCardMarketController destroy(){
        if(singleton != null){
            singleton = null;
        }

        return null;
    }

    /**
     * Called when te player selects a card they wish to buy. Checks whether the card and the slot selected are valid before proceeding to the payment.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void selectDevelopmentCard(ClientSelectDevelopmentCardMessage action, RemoteViewHandler view, User user){

        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            DevMarket developmentCardsMarket = model.getDevelopmentCardsMarket();

            DevelopmentCard targetCard = developmentCardsMarket.getDevelopmentCard(player, action.getRow(),action.getCol());
            DevelopmentCardSlot target = player.getDevelopmentCardSlots()[action.getTargetSlot().getValue()-1];


            try {
                // Check if player has enough resources at ALL. Assumes the card is presented with it's cost lowered if discounted.
                if (!player.canPay(targetCard.getCost())){throw new NotSufficientResourceException();}

                target.setTargetCard(targetCard, action.getRow(), action.getCol());
                view.handleStatusMessage(StatusMessage.CONTINUE);

            } catch (DevelopmentCardException e) {
                view.handleStatusMessage(StatusMessage.SELECTION_ERROR);
            } catch (NotSufficientResourceException e) {
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            } catch (NullPointerException e){
                view.handleStatusMessage(StatusMessage.EMPTY_ERROR);
            }
        }
    }

    /**
     * Called to actually buy the card and
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RealRemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void buyTargetCard(ClientBuyTargetCardMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {
            try {
                DevelopmentCardSlot slot = player.getDevelopmentCardSlots()[action.getSlotId().getValue() - 1];
                slot.check(true);
                //if check doesn't throw any exception i can proceed to buy the card
                //in this case i don't have to separate the two action as opposed to the production process of validation because i can buy only one card every turn
                player.toggleMainAction();
                slot.buyDevelopmentCard();
                DevelopmentCard card = model.getDevelopmentCardsMarket().buyDevelopmentCard(slot.getRow(), slot.getCol());


                //now i can remove the card from the market
                model.notify(new ServerBuyDevelopmentCardMessage(
                        model.getDevelopmentCardsMarket().getVisible(),
                        model.getCurrentPlayer().getVisibleSlots(),
                        card,
                        model.getUserFromPlayer(player)
                ));

                // Check if the win condition has been reached (player has acquired 7 dev cards).
                int cards = 0;
                for (DevelopmentCardSlot devSlot : player.getDevelopmentCardSlots()){
                    cards += devSlot.getSlot().size();
                }

                if (cards >= 7) throw new EndOfGameException(EndOfGameCause.SEVENTH_CARD, user);


            } catch (NotSufficientResourceException e) {
                try {
                    resourceController.resetResources(player, e.getTempResources());
                } catch (InvalidDepotException invalidDepotException) {
                    //if i enter this catch there is a problem in the way i am restoring the resources
                    invalidDepotException.printStackTrace();
                    view.handleStatusMessage(StatusMessage.SERVER_ERROR);
                }
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            }
        }
    }


    /**
     * This method is called by the LeaderCardsController when the conditions to activate
     * a leaderCard are verified.
     * @param player the Player activating the ability.
     * @param discount the resource discount granted by the ability.
     */
    public void addMarketAbility(Player player, Resource discount){
        model.setDevelopmentCardsMarket(model.getDevelopmentCardsMarket().addAbility(discount,player));
    }
}