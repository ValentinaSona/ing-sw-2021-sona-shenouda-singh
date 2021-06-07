package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.NotDecoratedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This interface helps implementing the decorator for the DevelopmentCardsMarket
 */
public interface DevMarket {

    DevelopmentCard buyDevelopmentCard(int row, int col);
    DevelopmentCard getDevelopmentCard(Player player, int row, int col);
    void shuffle();
    void discard(DevelopmentType color) throws EndOfGameException;
    DevelopmentCardDeck[][] getDecks();
    DevMarketView getVisible();
    DevMarket addAbility(Resource discount, Player player);
    Map<Player, List<Resource>> getMap() throws NotDecoratedException;

}
