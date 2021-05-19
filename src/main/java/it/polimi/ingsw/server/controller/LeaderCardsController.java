package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.IsNotYourTurnException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.Depot;
import it.polimi.ingsw.server.model.observable.DevelopmentCardSlot;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.model.observable.Strongbox;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientActivateSpecialAbilityMessage;

import java.util.ArrayList;

public class LeaderCardsController extends AbstractController{
    private final MarketController marketController;
    private static LeaderCardsController singleton;


    private LeaderCardsController(Model model){
        super(model);
        this.marketController = MarketController.getInstance(model);
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

        if( !(player.getTurn()) || !(player.getMainAction()) ){
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
     * This method is called whenever a player tries to activate a leaderCard
     * @param player
     * @param targetCard
     * @return
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

    private void useAbility(Player player, LeaderCard targetCard){
        SpecialAbility ability = targetCard.getSpecialAbility();

        if( ability instanceof WhiteMarbleAbility){
            WhiteMarbleAbility marbleAbility =(WhiteMarbleAbility) ability;
            marketController.addMarketAbility(player, marbleAbility.getMarble());
        }else if( ability instanceof ProductionAbility){
            ProductionAbility productionAbility = (ProductionAbility) ability;
            //devo aggiungere la production alla board
        }else if( ability instanceof ExtraDepotAbility){
            ExtraDepotAbility depotAbility = (ExtraDepotAbility) ability;
            player.addSpecialDepot(depotAbility.getCapacity(),depotAbility.getType());
        }else if( ability instanceof DiscountAbility){
            //devo aggiungere lo sconto alla board
        }else{
            new RuntimeException("We have some problem with the special ability");
        }

        targetCard.setActive(true);

    }

}
