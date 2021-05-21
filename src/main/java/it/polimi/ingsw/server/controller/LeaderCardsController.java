package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.Depot;
import it.polimi.ingsw.server.model.observable.DevelopmentCardSlot;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.model.observable.Strongbox;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientActivateSpecialAbilityMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientThrowLeaderCardMessage;

import java.util.ArrayList;

public class LeaderCardsController extends AbstractController{
    private final MarketController marketController;
    private final DevelopmentCardMarketController devController;
    private final ResourceController resourceController;
    private static LeaderCardsController singleton;


    private LeaderCardsController(Model model){
        super(model);
        this.marketController = MarketController.getInstance(model);
        this.devController = DevelopmentCardMarketController.getInstance(model);
        this.resourceController = ResourceController.getInstance(model);
    }

    public static LeaderCardsController getInstance(Model model){
        if(singleton == null){
            singleton = new LeaderCardsController(model);
        }

        return singleton;
    }

    /**
     * Called when the player is attempting to activate a Leader Card special ability.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void activateSpecialAbility(ClientActivateSpecialAbilityMessage action, RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn())  ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            LeaderCard targetCard = player.getLeaderCards().get(action.getLeaderId().getValue());
            boolean activate = checkRequirements(player, targetCard);

            if (activate) {
                useAbility(player, targetCard);
            } else {
                view.handleStatusMessage(StatusMessage.REQUIREMENTS_ERROR);
            }
        }
    }

    /**
     * Called when the player is discarding a leader card from their hand.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void throwLeaderCard(ClientThrowLeaderCardMessage action, RemoteViewHandler view, User user){

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn())  ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            LeaderCard targetCard = player.getLeaderCards().get(action.getLeaderId().getValue());
            player.getLeaderCards().remove(targetCard);
            resourceController.addFaithPoints(player,new Resource(1, ResourceType.FAITH));
        }
    }


    /**
     * This method is called whenever a player tries to activate a leaderCard
     * @param player Player whose leader card is being played.
     * @param targetCard LeaderCard being played.
     * @return true if card can be played, false otherwise.
     */
    private boolean checkRequirements(Player player, LeaderCard targetCard){

        Requirement[] requirements = targetCard.getRequirements();
        DevelopmentCardSlot[] developmentCardSlots = player.getDevelopmentCardSlots();
        ArrayList<Depot> warehouse = player.getWarehouse();
        Strongbox strongbox = player.getStrongbox();

        for (Requirement req : requirements){

            int required;
            int possessed = 0;

            if (!req.isResource()) {
                // Requirement is checking the development card level.
                required = req.getNumber();
                for (DevelopmentCardSlot slot : developmentCardSlots) {
                    possessed = (int) slot.getSlot().stream().filter(c -> c.getType() == req.getType() && (req.getLevel() == c.getLevel() || req.getLevel() == 0)).count();

                }

            } else {

                required = req.getResource().getQuantity();

                for (Depot depot: warehouse){
                    if (depot.getResource().getResourceType()==req.getResource().getResourceType()){
                        possessed += depot.getResource().getQuantity();
                    }
                }

                possessed += strongbox.getAvailableResources(req.getResource().getResourceType()).getQuantity();

            }
            if (required > possessed) {return false;}
        }
        return true;
    }

    /**
     * Called when the activation of a leader card is confirmed so that its special ability is implemented.
     * @param player player activating the card.
     * @param targetCard card being activated.
     */
    private void useAbility(Player player, LeaderCard targetCard){
        SpecialAbility ability = targetCard.getSpecialAbility();

        if( ability instanceof WhiteMarbleAbility){

            WhiteMarbleAbility marbleAbility =(WhiteMarbleAbility) ability;
            marketController.addMarketAbility(player, marbleAbility.getMarble());

        }else if( ability instanceof ProductionAbility){

            ProductionAbility productionAbility = (ProductionAbility) ability;
            player.addSpecialSlot(productionAbility.getCost());

        }else if( ability instanceof ExtraDepotAbility){

            ExtraDepotAbility depotAbility = (ExtraDepotAbility) ability;
            player.addSpecialDepot(depotAbility.getCapacity(),depotAbility.getType());

        }else if( ability instanceof DiscountAbility){

            DiscountAbility discountAbility = (DiscountAbility) ability;
            devController.addMarketAbility(player, discountAbility.getDiscount());

        }else{
            throw new RuntimeException("Unrecognized special ability type.");
        }

        targetCard.setActive(true);

    }



}