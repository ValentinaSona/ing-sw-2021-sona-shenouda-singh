package it.polimi.ingsw.server.model;

public class MarketView {

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
