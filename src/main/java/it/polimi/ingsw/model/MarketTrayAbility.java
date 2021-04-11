package it.polimi.ingsw.model;

import java.util.HashMap;

public class MarketTrayAbility implements Market {

    HashMap<Player, MarketMarble> abilityMap;

    Market marketTray;

    public MarketMarble[] getResources (Player player, int rowCol) { return null; }

    public void addAbility (MarketMarble marble, Player player) {}
}
