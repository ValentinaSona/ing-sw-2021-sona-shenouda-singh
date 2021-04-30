package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.TwoLeaderCardsException;

import java.util.*;
// TODO define getResources
// TODO make better comments
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

    // METODO DA DEFINIRE
    // È opportuno definire come il metodo chiede al player di selezionare l'abilità nel caso ne abbia 2 attive
    public MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException { return null; }

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

    public MarketMarble[] getChosen(MarketMarble[] choice){ return null; }
}
