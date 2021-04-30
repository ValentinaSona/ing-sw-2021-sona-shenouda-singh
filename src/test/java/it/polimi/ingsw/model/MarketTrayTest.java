package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MarketTrayTest {

    @Test
    void getResources() {

        Market market = MarketBuilder.build();
        MarketMarble[] resourceCheck = new MarketMarble[4];
        MarketMarble[][] array = market.getTray();
        System.arraycopy(array[0], 0, resourceCheck, 0, 4);
        assertArrayEquals(resourceCheck, market.getResources(null, 0));
        System.out.println(Arrays.deepToString(market.getTray()) + " Extra: " + market.getExtra());
        market.getResources(null, 6);
        System.out.println(Arrays.deepToString(market.getTray()) + " Extra: " + market.getExtra());

    }

    // TODO rewrite test with new player constructor
    /*
    @Test
    void addAbility() {
        Player jimmy = new Player();

        Market market = MarketBuilder.build();
        market = market.addAbility(MarketMarble.BLUE, jimmy);

        Assertions.assertTrue(market instanceof MarketTrayAbility);

    } */
}