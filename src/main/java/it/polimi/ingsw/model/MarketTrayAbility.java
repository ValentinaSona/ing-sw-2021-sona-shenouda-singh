package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.TwoLeaderCardsException;

import java.util.*;
// TODO make better comments
public class MarketTrayAbility extends AbstractModel implements Market {

    HashMap<Player, List<MarketMarble>> abilityMap;

    Market marketTray;

    /**
     * Constructor called by MarketTray when a player activates a WhiteMarbleAbility
     * @param marketTray the existing MarketTray
     * @param abilityMap the map containing players as key and a list of the corresponding WhiteMarbleAbility as value
     */
    public MarketTrayAbility (Market marketTray, HashMap<Player, List<MarketMarble>> abilityMap) {
        this.abilityMap = abilityMap;
        this.marketTray = marketTray;
    }

    // This constructor is called when a pre-existing game is loaded
    public MarketTrayAbility (MarketMarble[][] tray, MarketMarble extra, HashMap<Player, List<MarketMarble>> abilityMap) {

        this.marketTray = new MarketTray(tray, extra);
        this.abilityMap = abilityMap;

    }
    // TODO fix exception
    public MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException {

        if (abilityMap.containsKey(player)) {

            if (abilityMap.get(player).size() == 1) {
                MarketMarble[] resources = marketTray.getResources(player, rowCol);

                for (MarketMarble marble : resources) {
                    if (marble == MarketMarble.WHITE) marble = abilityMap.get(player).get(0);
                }

                return resources;
            }

            else {
                update("CHOOSE_MARKET_ABILITY", null, abilityMap.get(player).toArray(new MarketMarble[0]));
                throw new TwoLeaderCardsException();
            }
        }

        else return marketTray.getResources(player, rowCol);

    }

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

    public HashMap<Player, List<MarketMarble>> getAbilityMap() {
        return abilityMap;
    }
}
