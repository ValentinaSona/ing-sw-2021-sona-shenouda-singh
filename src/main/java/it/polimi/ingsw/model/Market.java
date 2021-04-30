package it.polimi.ingsw.model;
// TODO add getVisible method
public interface Market {

    MarketMarble[] getResources (Player player, int rowCol);

    Market addAbility (MarketMarble marble, Player player);

    MarketMarble[][] getTray();

    MarketMarble getExtra();

}
