package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardDeckTest {

    @Test
    void firstCard() {
        DevelopmentCardsMarket market = DevelopmentBuilder.build();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {
                DevelopmentCard card = market.getDecks()[i][j].firstCard();
                assertEquals(card, market.buyDevelopmentCards(i, j));

            }
        }
    }

    @Test
    void pickCard() {
        DevelopmentCardsMarket market = DevelopmentBuilder.build();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {
                int size1 = market.getDecks()[i][j].cardsLeft();
                market.getDecks()[i][j].pickCard();
                int size2 = market.getDecks()[i][j].cardsLeft();

                assertEquals(size1, size2+1);

            }
        }

    }
}