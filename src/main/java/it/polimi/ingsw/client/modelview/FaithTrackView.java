package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.FaithTrack;
import it.polimi.ingsw.server.model.PopeFavorTiles;

import java.io.Serializable;
import java.util.Arrays;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public class FaithTrackView implements Serializable {
    private final int faithMarker;
    private final PopeFavorTiles[] popeFavorTiles;

    //TODO: if we have this constructor then it should be
    public FaithTrackView(){
        this.popeFavorTiles = new PopeFavorTiles[]{PopeFavorTiles.DOWNWARDS, PopeFavorTiles.DOWNWARDS,PopeFavorTiles.DOWNWARDS};
        this.faithMarker = 0;
    }

    public FaithTrackView(int faithMarker, PopeFavorTiles[] favorTiles) {
        this.popeFavorTiles = favorTiles;
        this.faithMarker = faithMarker;
    }

    public FaithTrackView (FaithTrack faithTrack) {
        faithMarker = faithTrack.getFaithMarker();
        popeFavorTiles = faithTrack.getPopeFavorTiles();
    }

    public int getFaithMarker() {
        return faithMarker;
    }

    public PopeFavorTiles getPopeFavorTiles(int index) {
        return popeFavorTiles[index];
    }

    public PopeFavorTiles[] getPopeFavorTiles() {
        return popeFavorTiles;
    }



    public String toString() {

        String position = " ".repeat(2*faithMarker+1);
        String[] tiles = new String[3];
        position += ANSI_RED + CROSS + ANSI_RESET + " " + faithMarker + "\n";
        position += FAITH_TRACK ;
        for (int i = 0 ; i <3 ; i++){
            switch (popeFavorTiles[i]){
                case DOWNWARDS -> tiles[i] = " ";
                case UPWARDS -> tiles[i] = CHECK_MARK;
                case DISMISSED -> tiles[i] = "X";
            }
        }
        position +=  "          |--["+ tiles[0] +"]--|     |---["+ tiles[1]+"]---|   |----["+ tiles[2]+"]----|\n";
        return position;
    }
}
