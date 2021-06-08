package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.FaithTrack;
import it.polimi.ingsw.server.model.PopeFavorTiles;

import java.io.Serializable;
import java.util.Arrays;

public class FaithTrackView implements Serializable {
    private final int faithMarker;
    private final PopeFavorTiles[] popeFavorTiles;

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


    @Override
    public String toString() {



        return "FaithTrackView{" +
                "faithMarker=" + faithMarker +
                ", popeFavorTiles=" + Arrays.toString(popeFavorTiles) +
                '}';
    }
}
