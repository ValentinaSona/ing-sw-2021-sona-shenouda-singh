package it.polimi.ingsw.server.model;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

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

    /**
     * Builds the DevelopmentCardsMarket from a saved game
     * @param decks the saved decks
     * @param map the ability map for leader cards, if there are no saved abilities a empty map should be provided
     * @return the DevMarket
     */
    public static DevMarket build(DevelopmentCardDeck[][] decks, Map<Player, List<Resource>> map) {

        if (map.isEmpty()) return new DevelopmentCardsMarket(decks);
        else return new DevelopmentCardsMarketAbility(decks, map);
    }
    
}
