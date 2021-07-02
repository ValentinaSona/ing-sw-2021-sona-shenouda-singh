package it.polimi.ingsw.server.model;

import com.google.gson.Gson;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class follows the factory pattern and builds an instance of the development market
 */
public class DevelopmentBuilder {

    final private static String path = "config/devcards.json";
    final private static String completePath = "./src/main/resources/" + path;

    /**
     * Builds the standard DevelopmentCardsMarket importing it from file in the correct order and shuffles all the decks
     * @return the DevelopmentCardsMarket
     */
    public static DevelopmentCardsMarket build() {

        DevelopmentCardDeck[][] decks;

        Gson gson = new Gson();

        try {

            var file = new FileReader(completePath);
            decks =  gson.fromJson(file, DevelopmentCardDeck[][].class);

        } catch (FileNotFoundException e) {

            var input = new BufferedReader(new InputStreamReader(Objects.requireNonNull(DevelopmentBuilder.class.getClassLoader().getResourceAsStream(path))));
            decks =  gson.fromJson(input, DevelopmentCardDeck[][].class);

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
