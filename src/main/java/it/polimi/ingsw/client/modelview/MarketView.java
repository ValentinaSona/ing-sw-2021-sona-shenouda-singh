package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.MarketMarble;
import java.io.Serializable;

/**
 * View equivalent to the market class.
 */
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

    /**
     * Returns ANSI encoded string for printing in the CLI.
     * @return the printable market.
     */
    @Override
    public String toString() {

       StringBuilder marketPrint = new StringBuilder();

       for (int i = 0; i < 3; i++){
           marketPrint.append("\t");
           for (int j = 0; j < 4; j++){
               marketPrint.append(tray[i][j].toColorString());
           }
           if (i != 2) marketPrint.append("<- ").append(i + 1).append("\n");
           else  marketPrint.append("<- 3\t Extra: ");
       }
        marketPrint.append(extra.toColorString());
        marketPrint.append("\n");
        marketPrint.append("\t^ ^ ^ ^\n");
        marketPrint.append("\t| | | |\n");
        marketPrint.append("\t1 2 3 4\n");


        return marketPrint.toString();
    }
}
