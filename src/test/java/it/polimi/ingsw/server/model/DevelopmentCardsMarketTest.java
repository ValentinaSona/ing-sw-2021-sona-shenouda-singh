package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotDecoratedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardsMarketTest {

    @Test
    void buyDevelopmentCard() {
        DevMarket market = DevelopmentBuilder.build();
        var decks = market.getDecks();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {

                DevelopmentCard tempCard = decks[i][j].firstCard();
                assertEquals(tempCard, market.buyDevelopmentCard(i, j));
                assertNotEquals(tempCard, market.buyDevelopmentCard(i, j));
            }
        }

    }

    @Test
    void getDevelopmentCard() {
        var player = new Player("Lorenzo");

        DevMarket market = DevelopmentBuilder.build();
        var decks = market.getDecks();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {

                DevelopmentCard tempCard = decks[i][j].firstCard();
                assertEquals(tempCard, market.getDevelopmentCard(player, i, j));
                assertEquals(tempCard, market.getDevelopmentCard(player, i, j));
            }
        }
    }


    @Test
    void addAbility() {
        var player = new Player("Lorenzo");
        DevMarket market = DevelopmentBuilder.build();
        market = market.addAbility(new Resource(1, ResourceType.SHIELD), player);

        assertTrue(market instanceof DevelopmentCardsMarketAbility);

        try {
            var map = market.getMap();

            assertFalse(map.isEmpty());
            assertEquals(map.get(player).get(0), new Resource(1, ResourceType.SHIELD));
            assertEquals(1, map.get(player).size());

        } catch (NotDecoratedException e) {
            Assertions.fail();
        }
    }

}