package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.TwoLeaderCardsException;
import it.polimi.ingsw.server.model.Market;
import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.observable.MarketTrayAbility;
import it.polimi.ingsw.server.model.observable.Player;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MarketTrayAbilityTest {

    @Test
    void getResources() throws TwoLeaderCardsException {

        MarketMarble[][] exampleTray = {{MarketMarble.WHITE, MarketMarble.YELLOW, MarketMarble.GREY, MarketMarble.BLUE},
                {MarketMarble.BLUE, MarketMarble.PURPLE, MarketMarble.PURPLE, MarketMarble.WHITE},
                {MarketMarble.GREY, MarketMarble.YELLOW, MarketMarble.WHITE, MarketMarble.RED}};

        MarketMarble exampleExtra = MarketMarble.WHITE;

        HashMap<Player, List<MarketMarble>> abilities = new HashMap<>();
        Player jimmy = new Player("Jimmy");
        abilities.put(jimmy, new ArrayList<>(Arrays.asList(MarketMarble.BLUE)));

        Market market = new MarketTrayAbility(exampleTray, exampleExtra, abilities);

        MarketMarble[] resources;
        resources = market.getResources(jimmy, 4);
        assertArrayEquals(new MarketMarble[]{MarketMarble.YELLOW, MarketMarble.PURPLE, MarketMarble.YELLOW}, resources);
        assertEquals(market.getExtra(), MarketMarble.YELLOW);

        resources = market.getResources(jimmy, 0);
        assertArrayEquals(new MarketMarble[]{MarketMarble.BLUE, MarketMarble.PURPLE, MarketMarble.GREY, MarketMarble.BLUE}, resources);
        assertEquals(MarketMarble.WHITE, market.getExtra());

        market = market.addAbility(MarketMarble.PURPLE, jimmy);
        Market finalMarket = market;
        assertThrows(TwoLeaderCardsException.class, () -> finalMarket.getResources(jimmy, 4));
    }

    @Test
    void addAbility() {
    }

    @Test
    void getChosen() {
    }

}