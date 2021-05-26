package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.server.exception.TwoLeaderCardsException;

import java.util.*;
import java.util.stream.Stream;


public class MarketTrayAbility implements Market {

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

    /**
     * Constructor called when a saved game is loaded and at least one WhiteMarbleAbility was active before saving the game
     * @param tray the saved market tray
     * @param extra the saved extra marble
     * @param abilityMap the map with players and their respective WhiteMarbleAbility (if any)
     */
    public MarketTrayAbility (MarketMarble[][] tray, MarketMarble extra, HashMap<Player, List<MarketMarble>> abilityMap) {

        this.marketTray = new MarketTray(tray, extra);
        this.abilityMap = abilityMap;

    }

    /**
     * Method called when a players wants to get resources from the market
     * @param player the player requesting the resources
     * @param rowCol the row/column where to take the resources
     * @return the array of the chosen resources, if the player has ONE WhiteMarbleAbility activated it gets the array already modified by the ability
     * @throws TwoLeaderCardsException if the player has two WhiteMarbleAbility active at the same time, updating the view
     */
    public MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException {

        MarketMarble[] resources = marketTray.getResources(player, rowCol);

        if (abilityMap.containsKey(player) && Stream.of(resources).anyMatch(m -> m == MarketMarble.WHITE)) {

            if (abilityMap.get(player).size() == 1) {
                MarketMarble ability = abilityMap.get(player).get(0);

                return Stream.of(resources)
                        .map(marble -> marble==MarketMarble.WHITE? ability : marble)
                        .toArray(MarketMarble[]::new);
            }

            else {
                //TODO al momento ho messo un valore a caso
                //qunate biglie bianche ci sono ?? passalo come argomento dell'exception
                throw new TwoLeaderCardsException(4);
            }
        }

        else return resources;

    }

    /**
     * Called when a player activates a new WhiteMarbleAbility. Adds the player and the ability to the map
     * @param marble the activated ability
     * @param player the player who activated the ability
     * @return the market itself
     */
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

    //TODO
    @Override
    public MarketView getVisible() {
        return null;
    }
}
