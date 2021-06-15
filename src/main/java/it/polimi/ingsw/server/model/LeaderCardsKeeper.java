package it.polimi.ingsw.server.model;

import com.google.gson.Gson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LeaderCardsKeeper {

    final private static String path = "./src/main/resources/config/leadercards.json";

    List<LeaderCard> leaderCards;

    //TODO reminder to test this class to see if the file was created correctly
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
        //Gson gson = new Gson();
        try {

            var file = new FileReader(path);
            leaderCards =  Stream.of(gson.fromJson(file, LeaderCard[].class)).collect(Collectors.toList());

        } catch (FileNotFoundException e) {

            var input = new BufferedReader(new InputStreamReader(LeaderCardsKeeper.class.getClassLoader().getResourceAsStream(path)));
            leaderCards =  Stream.of(gson.fromJson(input, LeaderCard[].class)).collect(Collectors.toList());

        }
            // Json file added, still has to be tested

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
