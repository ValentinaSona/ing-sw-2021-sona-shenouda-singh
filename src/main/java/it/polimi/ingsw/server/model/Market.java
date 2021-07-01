package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.MarketView;
import it.polimi.ingsw.server.exception.NotDecoratedException;
import it.polimi.ingsw.server.exception.TwoLeaderCardsException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Market {

    MarketMarble[] getResources (Player player, int rowCol) throws TwoLeaderCardsException;

    Market addAbility (MarketMarble marble, Player player);

    MarketMarble[][] getTray();

    MarketMarble getExtra();

    MarketMarble[] getChosen(MarketMarble[] choice) throws NotDecoratedException;

    Map<Player, List<MarketMarble>> getAbilityMap() throws NotDecoratedException;

    MarketView getVisible();

}
