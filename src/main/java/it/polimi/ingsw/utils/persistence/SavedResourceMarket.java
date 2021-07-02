package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.MarketMarble;
import it.polimi.ingsw.server.model.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SavedResourceMarket {

    private final MarketMarble[][] savedTray;
    private final MarketMarble savedExtra;
    private final Map<String, List<MarketMarble>> savedAbilityMap;

    public SavedResourceMarket(MarketMarble[][] trayToSave, MarketMarble extraToSave, Map<Player, List<MarketMarble>> mapToSave) {
        savedTray = trayToSave;
        savedExtra = extraToSave;
        savedAbilityMap = mapToSave.entrySet().stream().collect(Collectors.toMap(
                e ->  e.getKey().getNickname(),
                Map.Entry::getValue
        ));
    }

    public MarketMarble[][] getSavedTray() {
        return savedTray;
    }

    public MarketMarble getSavedExtra() {
        return savedExtra;
    }

    public Map<String, List<MarketMarble>> getSavedAbilityMap() {
        return savedAbilityMap;
    }
}
