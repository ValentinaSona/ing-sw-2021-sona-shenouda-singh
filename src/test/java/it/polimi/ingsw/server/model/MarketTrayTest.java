package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.MarketTray;
import it.polimi.ingsw.server.model.observable.MarketTrayAbility;
import it.polimi.ingsw.server.model.observable.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketTrayTest {

    @Test
    void getResources() {

        MarketMarble[][] exampleTray = {{MarketMarble.WHITE, MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE},
                {MarketMarble.BLUE, MarketMarble.PURPLE, MarketMarble.PURPLE, MarketMarble.WHITE},
                {MarketMarble.GREY, MarketMarble.YELLOW, MarketMarble.WHITE, MarketMarble.RED}};

        MarketMarble exampleExtra = MarketMarble.WHITE;

        MarketTray market = new MarketTray(exampleTray, exampleExtra);

        MarketMarble[] resources = market.getResources(null, 0);
        assertArrayEquals(resources, new MarketMarble[]{MarketMarble.WHITE, MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE});
        assertEquals(market.getExtra(), MarketMarble.WHITE);

        resources = market.getResources(null, 0);
        assertArrayEquals(resources, new MarketMarble[]{MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE, MarketMarble.WHITE});
        assertEquals(market.getExtra(), MarketMarble.YELLOW);

        resources = market.getResources(null, 6);
        assertArrayEquals(resources, new MarketMarble[]{MarketMarble.WHITE, MarketMarble.WHITE, MarketMarble.RED});
        assertEquals(market.getExtra(), MarketMarble.WHITE);

        resources = market.getResources(null, 6);
        assertArrayEquals(resources, new MarketMarble[]{MarketMarble.WHITE, MarketMarble.RED, MarketMarble.YELLOW});
        assertEquals(market.getExtra(), MarketMarble.WHITE);

    }

    @Test
    void addAbility() {
        Player jimmy = new Player("0, 0, null");

        Market market = MarketBuilder.build();
        market = market.addAbility(MarketMarble.BLUE, jimmy);

        Assertions.assertTrue(market instanceof MarketTrayAbility);

        HashMap<Player, List<MarketMarble>> map = market.getAbilityMap();

        Assertions.assertTrue(map.containsKey(jimmy));
        assertEquals(map.get(jimmy), Collections.singletonList(MarketMarble.BLUE));

    }

    @Test
    void getTray() {
        MarketMarble[][] exampleTray = {{MarketMarble.WHITE, MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE},
                {MarketMarble.BLUE, MarketMarble.PURPLE, MarketMarble.PURPLE, MarketMarble.WHITE},
                {MarketMarble.GREY, MarketMarble.YELLOW, MarketMarble.WHITE, MarketMarble.RED}};

        MarketMarble exampleExtra = MarketMarble.WHITE;

        MarketTray market = new MarketTray(exampleTray, exampleExtra);

        assertArrayEquals(market.getTray(), new MarketMarble[][]{{MarketMarble.WHITE, MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE},
                {MarketMarble.BLUE, MarketMarble.PURPLE, MarketMarble.PURPLE, MarketMarble.WHITE},
                {MarketMarble.GREY, MarketMarble.YELLOW, MarketMarble.WHITE, MarketMarble.RED}});
    }
}