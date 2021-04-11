package it.polimi.ingsw.model;

public interface Market {

    public MarketMarble[] getResources (Player player, int rowCol);

    public void addAbility (MarketMarble marble, Player player);

}
