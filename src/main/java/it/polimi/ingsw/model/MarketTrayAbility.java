package it.polimi.ingsw.model;

import java.util.*;

public class MarketTrayAbility implements Market {

    HashMap<Player, List<MarketMarble>> abilityMap;

    Market marketTray;

    // This constructor is called when the first white marble ability is added
    public MarketTrayAbility (Market marketTray, HashMap<Player, List<MarketMarble>> abilityMap) {
        this.abilityMap = abilityMap;
        this.marketTray = marketTray;
    }

    // This constructor is called when a pre-existing game is loaded
    public MarketTrayAbility (MarketMarble[][] tray, MarketMarble extra, HashMap<Player, List<MarketMarble>> abilityMap) {

        this.marketTray = new MarketTray(tray, extra);
        this.abilityMap = abilityMap;

    }

    // Da definire
    public MarketMarble[] getResources (Player player, int rowCol) { return null; }

    public Market addAbility (MarketMarble marble, Player player) {

        if (abilityMap.containsKey(player)) abilityMap.get(player).add(marble);

        else {
            List<MarketMarble> playerAbilities = new ArrayList<>();
            playerAbilities.add(marble);
            abilityMap.put(player, playerAbilities);
        }

        return this;

    }

    public MarketMarble[][] getTray() { return marketTray.getTray(); }

    public MarketMarble getExtra() { return marketTray.getExtra(); }
}
