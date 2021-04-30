package it.polimi.ingsw.model;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class DevelopmentBuilder {

    final private static String path = "./src/main/resources/config/devcards.json";

    /**
     * Builds the standard DevelopmentCardsMarket importing it from file in the correct order and shuffles all the decks
     * @return the DevelopmentCardsMarket
     */
    public static DevelopmentCardsMarket build() {

        DevelopmentCardDeck[][] decks = new DevelopmentCardDeck[3][4];

        Gson gson = new Gson();
        try {

            decks =  gson.fromJson(new FileReader(path), DevelopmentCardDeck[][].class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DevelopmentCardsMarket market = new DevelopmentCardsMarket(decks);
        market.shuffle();

        return market;
    }

    // TODO: build method overload
    
}
