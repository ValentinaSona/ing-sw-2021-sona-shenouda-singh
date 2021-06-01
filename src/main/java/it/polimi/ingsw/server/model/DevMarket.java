package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.DevMarketView;

import java.util.HashMap;
import java.util.List;

/**
 * This interface helps implementing the decorator for the DevelopmentCardsMarket
 */
public interface DevMarket {

    DevelopmentCard buyDevelopmentCard(int row, int col);
    DevelopmentCard getDevelopmentCard(Player player, int row, int col);
    void shuffle();
    DevelopmentCardDeck[][] getDecks();
    DevMarketView getVisible();
    DevMarket addAbility(Resource discount, Player player);
    HashMap<Player, List<Resource>> getMap();

}
