package it.polimi.ingsw.server.model;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class follows the factory pattern and creates an instance of the resource market
 */
public class MarketBuilder {

    private static SecureRandom random = new SecureRandom(); // Initialising random

    /**
     * Builds the standard market, should be called at the start of a new fresh game
     * @return the market with the standard set of marbles in random positions
     */
    public static Market build () {

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

    /**
     * This method is called when a saved game is loaded and initialized
     * @param importedTray saved market tray
     * @param importedExtra saved extra marble
     * @param playerAbilities saved map of player WhiteMarbleAbilities, in case of no abilities a empty map should be provided
     * @return the constructed market, which could be a marketTray or a MarketTrayAbility depending of the state of the saved game
     */
    public static Market build ( MarketMarble[][] importedTray, MarketMarble importedExtra, Map<Player, List<MarketMarble>> playerAbilities) {

        if (playerAbilities.isEmpty()) { // If there are no active abilities in the market
            return new MarketTray(importedTray, importedExtra);
        }

        else{
            return new MarketTrayAbility(importedTray, importedExtra, playerAbilities);
        }
    }

    /**
     * This methods places a set of marbles randomly into a 3x4 array
     * @param marbleSet the map of the set of marbles, mapping color to quantity
     * @return the randomized array
     */
    private static MarketMarble[][] randomize (HashMap<MarketMarble, Integer> marbleSet) {
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

    /**
     * This method should be called after randomize. It takes out the last marble from the marble map. Its purpose is for determining the extra marble in the market
     * @param marbleSet the map of marbles
     * @return the last marble remaining
     */
    private static MarketMarble getLast (HashMap<MarketMarble, Integer> marbleSet) {

        // The loop finds the remaining marble and assigns it to extra
        for(MarketMarble temp : MarketMarble.values()) {

            if (marbleSet.containsKey(temp)) {
                return temp;
            }
        }

        return null;

    }
}
