package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MarketBuilderTest {

    @Test
    void buildMarket() {
        Market tray = MarketBuilder.buildMarket();
        System.out.println(Arrays.deepToString(tray.getTray()));
        System.out.println(tray.getExtra());
        MarketMarble[] choice = tray.getResources(null, 2);
        System.out.println(Arrays.deepToString(tray.getTray()));
        System.out.println(tray.getExtra());
    }

    @Test
    void testBuildMarket() {
    }

    @Test
    void randomize() {
    }

    @Test
    void getLast() {
    }
}