package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.TwoLeaderCardsException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MarketBuilderTest {

    @Test
    void build() {
        Market tray = MarketBuilder.build();
        System.out.println(Arrays.deepToString(tray.getTray()));
        System.out.println(tray.getExtra());
        try {
            MarketMarble[] choice = tray.getResources(null, 2);
        } catch (TwoLeaderCardsException e) {
            e.printStackTrace();
        }
        System.out.println(Arrays.deepToString(tray.getTray()));
        System.out.println(tray.getExtra());
    }

    @Test
    void getLast() {
    }
}