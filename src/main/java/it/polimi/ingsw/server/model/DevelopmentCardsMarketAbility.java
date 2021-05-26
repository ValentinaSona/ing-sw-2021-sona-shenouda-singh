package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DevelopmentCardsMarketAbility implements DevMarket {

    DevelopmentCardsMarket cardsMarket;
    HashMap<Player, List<Resource>> abilityMap;

    public DevelopmentCardsMarketAbility (DevelopmentCardsMarket cardsMarket, HashMap<Player, List<Resource>> abilityMap) {
        this.cardsMarket = cardsMarket;
        this.abilityMap = abilityMap;
    }

    /**
     * Standard DevMarket method, removes the selected cards and returns it
     * @param row row of the deck grid
     * @param col column of the deck grid
     * @return the chosen card
     */
    public DevelopmentCard buyDevelopmentCard(int row, int col) {
        return cardsMarket.buyDevelopmentCard(row, col);
    }

    /**
     * Works like the corresponding DevelopmentCardsMarket method, but if the player has a DiscountAbility activated
     * it applies the discount directly on the card (if possible)
     * @param player the player requesting the card
     * @param row row of the deck grid
     * @param col column of the deck grid
     * @return the chosen card with discounts applied (if any)
     */
    public DevelopmentCard getDevelopmentCard(Player player, int row, int col) {

        DevelopmentCard card = cardsMarket.getDevelopmentCard(player, row, col);

        if (abilityMap.containsKey(player)) {

            Resource[] cost = card.getCost();

            for(Resource r : abilityMap.get(player)) {
                for(Resource cardCost : cost) {
                    if (cardCost.getResourceType() == r.getResourceType()) cardCost.sub(r);
                }
            }

            return Arrays.equals(cost, card.getCost()) ? card : new DevelopmentCard(cost, card);
        }

        else return card;
    }

    /**
     * Shuffles all the decks
     */
    public void shuffle() {
        cardsMarket.shuffle();
    }

    public DevelopmentCardDeck[][] getDecks() {
        return cardsMarket.getDecks();
    }

    public DevelopmentCard[][] getVisible() {
        return cardsMarket.getVisible();
    }

    /**
     * Method called when a player activates a new DiscountAbility
     * @param discount the resource associated with the ability
     * @param player the player who activated the ability
     * @return this
     */
    public DevMarket addAbility(Resource discount, Player player) {
        if (abilityMap.containsKey(player)) abilityMap.get(player).add(discount);
        else abilityMap.put(player, new ArrayList<>(Arrays.asList(discount)));

        return this;
    }

    public HashMap<Player, List<Resource>> getMap() {
        return abilityMap;
    }
}
