package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.DevelopmentCard;


import java.io.Serializable;
import java.util.Arrays;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public class DevMarketView implements Serializable {
    final private DevelopmentCard[][] tray;

    public DevMarketView(DevelopmentCard[][] tray) {
        this.tray = tray;
    }

    public DevelopmentCard[][] getTray() {
        return tray;
    }

    @Override
    public String toString() {

        String marketPrint = "";


        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 3; j++) {

                marketPrint+= tray[j][i] + "\n";
                if( i == 3 && j != 2) marketPrint+="\n\n";
            }

        }



        return marketPrint;
    }
}
