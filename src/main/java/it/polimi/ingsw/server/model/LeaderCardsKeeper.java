package it.polimi.ingsw.server.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class handles the leader cards imports and picks
 */
public class LeaderCardsKeeper {

    final private static String path = "config/leadercards.json";
    final private static String completePath = "./src/main/resources/" + path;

    List<LeaderCard> leaderCards;

    /**
     * This constructor imports from file all the leader cards, then stores them in a list and shuffles them
     */
    public LeaderCardsKeeper() {

      RuntimeTypeAdapterFactory<SpecialAbility> shapeAdapterFactory = RuntimeTypeAdapterFactory
                .of(SpecialAbility.class, "abilityType")
                .registerSubtype(DiscountAbility.class, "discount")
                .registerSubtype(WhiteMarbleAbility.class, "marble")
                .registerSubtype(ExtraDepotAbility.class, "depot")
                .registerSubtype(ProductionAbility.class, "production");
        Gson gson = new GsonBuilder()
                        .registerTypeAdapterFactory(shapeAdapterFactory)
                        .create();
        try {

            var file = new FileReader(completePath);
            leaderCards =  Stream.of(gson.fromJson(file, LeaderCard[].class)).collect(Collectors.toList());

        } catch (FileNotFoundException e) {

            var input = new BufferedReader(new InputStreamReader(Objects.requireNonNull(LeaderCardsKeeper.class.getClassLoader().getResourceAsStream(path), "LeaderCardsKeeper file could not be loaded")));
            leaderCards =  Stream.of(gson.fromJson(input, LeaderCard[].class)).collect(Collectors.toList());

        }

        Collections.shuffle(leaderCards);
    }

    /**
     * @return the first 4 cards in the leader cards deck
     */
    public LeaderCard[] pickFour() {
        LeaderCard[] cards = new LeaderCard[4];

        for(int i=0; i < 4; i++) {
            cards[i] = leaderCards.get(0);
            leaderCards.remove(0);
        }

        return cards;
    }
}
