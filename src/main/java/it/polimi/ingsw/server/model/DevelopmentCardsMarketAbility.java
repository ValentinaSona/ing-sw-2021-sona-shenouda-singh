package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.server.exception.EndOfGameException;

import java.util.*;

/**
 * This class is a decorator of the DevelopmentCardsMarket class, and considers any DiscountAbility if a player has one
 */
public class DevelopmentCardsMarketAbility implements DevMarket {

    private DevelopmentCardsMarket cardsMarket;
    private Map<Player, List<Resource>> abilityMap;

    public DevelopmentCardsMarketAbility (DevelopmentCardsMarket cardsMarket, Map<Player, List<Resource>> abilityMap) {
        this.cardsMarket = cardsMarket;
        this.abilityMap = abilityMap;
    }

    public DevelopmentCardsMarketAbility (DevelopmentCardDeck[][] cards, Map<Player, List<Resource>> abilityMap) {
        this.cardsMarket = new DevelopmentCardsMarket(cards);
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

        if (abilityMap.containsKey(player) && card != null) {

            Resource[] cost = new Resource[card.getCost().length];

            for (int i = 0; i < card.getCost().length; i++){
                cost[i] = new Resource(card.getCost()[i].getQuantity(),card.getCost()[i].getResourceType());
            }

            for(Resource r : abilityMap.get(player)) {
                for(Resource cardCost : cost) {
                    if (cardCost.getResourceType() == r.getResourceType()) cardCost.sub(r);
                }
            }

            return new DevelopmentCard(cost, card);
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

    public DevMarketView getVisible() {
        return cardsMarket.getVisible();
    }

    public void discard (DevelopmentType color) throws EndOfGameException {cardsMarket.discard(color);}

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

    public Map<Player, List<Resource>> getMap() {
        return abilityMap;
    }
}
