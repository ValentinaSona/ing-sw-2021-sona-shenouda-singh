package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ExtraDepotAbility;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.WhiteMarbleAbility;

public class LeaderCardsController extends AbstractController{
    private final Market market;

    public LeaderCardsController(Market market) {
        this.market = market;
    }

    private void activateSpecialAbility(ExtraDepotAbility ability){
        getCurrentPlayer().addSpecialDepot(ability.getCapacity(), ability.getType());
    }

    private void activateSpecialAbility(WhiteMarbleAbility ability){
        market.addAbility(ability.getMarble(), getCurrentPlayer());
    }





}
