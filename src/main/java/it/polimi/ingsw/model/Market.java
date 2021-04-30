package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.TwoLeaderCardsException;

// TODO add getVisible method
public interface Market {

    MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException;

    void insertIntoMatrix (int rowCol);

    Market addAbility (MarketMarble marble, Player player);

    MarketMarble[][] getTray();

    MarketMarble getExtra();

    MarketMarble[] getChosen(MarketMarble[] choice);

}
