package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MarketBuilder {

    // Builds the standard market, should be used at the beginning of a new game
    public static Market buildMarket () {

        // Construction of MarketTray with the standard array of marbles
        // key: type of marble, value: number of those marbles
        HashMap<MarketMarble, Integer> marbleSet = new HashMap<>();
        marbleSet.put(MarketMarble.WHITE, 4);
        marbleSet.put(MarketMarble.BLUE, 2);
        marbleSet.put(MarketMarble.GREY, 2);
        marbleSet.put(MarketMarble.YELLOW, 2);
        marbleSet.put(MarketMarble.PURPLE, 2);
        marbleSet.put(MarketMarble.RED, 1);

        MarketMarble[][] marbleTray = randomize(marbleSet);
        MarketMarble extra = getLast(marbleSet);

        return new MarketTray(marbleTray, extra);

    }

    // This method is called when a saved game loaded and initialized
    public static Market buildMarket ( MarketMarble[][] importedTray, MarketMarble importedExtra, HashMap<Player, List<MarketMarble>> playerAbilities) {

        if (playerAbilities.isEmpty()) { // If there are no active abilities in the market
            return new MarketTray(importedTray, importedExtra);
        }

        else{
            return new MarketTrayAbility(importedTray, importedExtra, playerAbilities);
        }
    }

    public static MarketMarble[][] randomize (HashMap<MarketMarble, Integer> marbleSet) {

        Random random = new Random(); // Initialising random
        MarketMarble[][] marbleTray = new MarketMarble[3][4];

        // First an array with all the marbles values is created
        MarketMarble[] marbles = MarketMarble.values();
        MarketMarble tempMarble = marbles[random.nextInt(marbles.length)]; // A random marble is chosen

        // Loops through all the matrix
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {

                // If the random marble is not present in the map, another random marble is generated until the marble is present in the map
                while(!marbleSet.containsKey(tempMarble)) tempMarble = marbles[random.nextInt(marbles.length)];

                marbleTray[row][col] = tempMarble;

                // After putting the marble in the market tray, the corresponding quantity is decreased
                if(marbleSet.get(tempMarble) > 1) {
                    marbleSet.replace(tempMarble, marbleSet.get(tempMarble)-1);
                }
                // The marble gets deleted from the map if there was only one left
                else {
                    marbleSet.remove(tempMarble);
                }

                // A new random marble is generated
                tempMarble = marbles[random.nextInt(marbles.length)];

            }
        }

        return marbleTray;

    }

    public static MarketMarble getLast (HashMap<MarketMarble, Integer> marbleSet) {

        // The loop finds the remaining marble and assigns it to extra
        for(MarketMarble temp : MarketMarble.values()) {

            if (marbleSet.containsKey(temp)) {
                return temp;
            }
        }

        return null;

    }
}
