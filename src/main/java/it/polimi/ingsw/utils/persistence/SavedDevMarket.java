package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.DevelopmentCardDeck;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds all the relevant data of the development market, without unnecessary nesting
 */
public class SavedDevMarket {

    private final DevelopmentCardDeck[][] savedDecks;
    private final Map<String, List<Resource>> savedAbilityMap;

    public SavedDevMarket(DevelopmentCardDeck[][] decksToSave, Map<Player, List<Resource>> mapToSave) {
        savedDecks = decksToSave;
        savedAbilityMap = mapToSave.entrySet().stream().collect(Collectors.toMap(
                e ->  e.getKey().getNickname(),
                Map.Entry::getValue
        ));
    }

    public DevelopmentCardDeck[][] getSavedDecks() {
        return savedDecks;
    }

    public Map<String, List<Resource>> getSavedAbilityMap() {
        return savedAbilityMap;
    }

}
