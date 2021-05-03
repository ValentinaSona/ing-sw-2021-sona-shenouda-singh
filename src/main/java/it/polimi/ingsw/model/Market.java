package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.TwoLeaderCardsException;

import java.util.HashMap;
import java.util.List;

// TODO add getVisible method
public interface Market {

    MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException;

    Market addAbility (MarketMarble marble, Player player);

    MarketMarble[][] getTray();

    MarketMarble getExtra();

    MarketMarble[] getChosen(MarketMarble[] choice);

    HashMap <Player, List<MarketMarble>> getAbilityMap();

}
