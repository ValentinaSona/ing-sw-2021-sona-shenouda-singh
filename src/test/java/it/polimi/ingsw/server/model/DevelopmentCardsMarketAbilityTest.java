package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardsMarketAbilityTest {

    @Test
    void getDevelopmentCard() {
        var player = new Player("Lorenzo");
        var deck = new DevelopmentCardDeck[1][1];
        var card = new DevelopmentCard(1, new Resource[]{new Resource(3, ResourceType.SHIELD)}, DevelopmentType.BLUE, 1, 1, null);
        deck[0][0] = new DevelopmentCardDeck(Collections.singletonList(card), 1, DevelopmentType.BLUE);

        DevMarket market = new DevelopmentCardsMarket(deck);

        market = market.addAbility(new Resource(1, ResourceType.SHIELD), player);

        var devCard = market.getDevelopmentCard(player, 0, 0);



        assertEquals(devCard.getCost()[0].getQuantity(), 2);
        assertEquals(devCard.getCost()[0].getResourceType(), ResourceType.SHIELD);
    }
}