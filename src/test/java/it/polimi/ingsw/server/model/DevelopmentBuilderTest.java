package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.DevelopmentBuilder;
import it.polimi.ingsw.server.model.DevelopmentCardsMarket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentBuilderTest {

    @Test
    void build() {
        DevelopmentCardsMarket market = DevelopmentBuilder.build();

        assertEquals(market.getDecks().length, 3);
        assertEquals(market.getDecks()[0].length, 4);

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {
                System.out.print("Level: " + market.getDecks()[i][j].getLevel() + " Type: " + market.getDecks()[i][j].getColor() + "\t\t" );
                assertEquals(market.getDecks()[i][j].firstCard().getLevel(), market.getDecks()[i][j].getLevel());
                assertEquals(market.getDecks()[i][j].firstCard().getType(), market.getDecks()[i][j].getColor());

            }
            System.out.print("\n");
        }
    }
}