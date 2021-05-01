package it.polimi.ingsw.model;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeaderCardsKeeper {

    final private static String path = "./src/main/resources/config/leadercards.json";

    List<LeaderCard> leaderCards;

    public LeaderCardsKeeper() {

        Gson gson = new Gson();
        try {

            leaderCards =  Arrays.asList(gson.fromJson(new FileReader(path), LeaderCard[].class));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
