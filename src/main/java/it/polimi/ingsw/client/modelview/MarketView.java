package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.MarketMarble;

import java.io.Serializable;

public class MarketView implements Serializable {

    final private MarketMarble[][] tray;
    final private MarketMarble extra;

    public MarketView (MarketMarble[][] tray, MarketMarble extra) {
        this.tray = tray;
        this.extra = extra;
    }

    public MarketMarble[][] getTray() {
        return tray;
    }

    public MarketMarble getExtra() {
        return extra;
    }
}
