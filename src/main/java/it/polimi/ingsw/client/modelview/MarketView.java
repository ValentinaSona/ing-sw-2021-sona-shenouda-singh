package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.MarketMarble;
import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;
import java.io.Serializable;
import java.util.Arrays;

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

    @Override
    public String toString() {

       String marketPrint = "";

       for (int i = 0; i < 3; i++){
           marketPrint += "\t";
           for (int j = 0; j < 4; j++){
               marketPrint += tray[i][j].toColorString();
           }
           if (i != 2) marketPrint += "<- " + (i+1) + "\n";
           else  marketPrint += "<- 3\t Extra: ";
       }
        marketPrint += extra.toColorString();
        marketPrint += "\n";
        marketPrint += "\t^ ^ ^ ^\n";
        marketPrint += "\t| | | |\n";
        marketPrint += "\t1 2 3 4\n";


        return marketPrint;
    }
}
