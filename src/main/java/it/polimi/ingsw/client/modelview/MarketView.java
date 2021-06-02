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
               String marble = "";
               switch (tray[i][j]){
                   case RED -> {marble = ANSI_RED + CIRCLE + ANSI_RESET + " ";}
                   case WHITE -> {marble =  WHITE_CIRCLE +  " ";}
                   case BLUE -> {marble = ANSI_BLUE + CIRCLE + ANSI_RESET + " ";}
                   case YELLOW -> {marble = ANSI_YELLOW + CIRCLE + ANSI_RESET + " ";}
                   case GREY -> {marble = ANSI_WHITE + CIRCLE + ANSI_RESET + " ";}
                   case PURPLE -> {marble = ANSI_PURPLE + CIRCLE + ANSI_RESET + " ";}

               }
               marketPrint += marble;
           }
           if (i != 2) marketPrint += "\n";
           else  marketPrint += "\t Extra: ";
       }
        String marble = "";
        switch (extra){
            case RED -> {marble = ANSI_RED + CIRCLE + ANSI_RESET + " ";}
            case WHITE -> {marble =  WHITE_CIRCLE +  " ";}
            case BLUE -> {marble = ANSI_BLUE + CIRCLE + ANSI_RESET + " ";}
            case YELLOW -> {marble = ANSI_YELLOW + CIRCLE + ANSI_RESET + " ";}
            case GREY -> {marble = ANSI_WHITE+ CIRCLE + ANSI_RESET + " ";}
            case PURPLE -> {marble = ANSI_PURPLE + CIRCLE + ANSI_RESET + " ";}

        }
        marketPrint += marble;
        marketPrint += "\n";


        return marketPrint;
    }
}
