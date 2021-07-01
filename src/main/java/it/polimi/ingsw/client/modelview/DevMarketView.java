package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.DevelopmentCard;

import java.io.Serializable;

/**
 * View equivalent of the development card market.
 */
public class DevMarketView implements Serializable {
    final private DevelopmentCard[][] tray;

    public DevMarketView(DevelopmentCard[][] tray) {
        this.tray = tray;
    }

    public DevelopmentCard[][] getTray() {
        return tray;
    }

    /**
     * Returns an ANSI colored string
     * @return the printable market string.
     */
    @Override
    public String toString() {

        StringBuilder marketPrint = new StringBuilder();
        String empty =  "===============\n"+
                        "==== Empty ====\n"+
                        "===============\n";
        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 3; j++) {

                marketPrint.append((tray[j][i] != null) ? tray[j][i] : empty).append("\n");
                if( i != 3 && j == 2) marketPrint.append("\n\n");
            }

        }



        return marketPrint.toString();
    }
}
