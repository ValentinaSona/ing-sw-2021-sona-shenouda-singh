package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.observable.Player;

import java.util.HashMap;
import java.util.List;

/**
 * This interface helps implementing the decorator for the DevelopmentCardsMarket
 */
public interface DevMarket {

    DevelopmentCard buyDevelopmentCards(int row, int col);
    DevelopmentCard getDevelopmentCards(Player player, int row, int col);
    void shuffle();
    DevelopmentCardDeck[][] getDecks();
    DevelopmentCard[][] getVisible();
    DevMarket addAbility(Resource discount, Player player);
    HashMap<Player, List<Resource>> getMap();

}
