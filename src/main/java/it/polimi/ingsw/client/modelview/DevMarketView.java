package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.DevelopmentCard;


import java.io.Serializable;

public class DevMarketView implements Serializable {
    final private DevelopmentCard[][] tray;

    public DevMarketView(DevelopmentCard[][] tray) {
        this.tray = tray;
    }

    public DevelopmentCard[][] getTray() {
        return tray;
    }

}
