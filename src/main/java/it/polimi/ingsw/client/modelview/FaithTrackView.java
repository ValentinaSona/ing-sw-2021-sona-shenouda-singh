package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.FaithTrack;
import it.polimi.ingsw.server.model.PopeFavorTiles;

import java.io.Serializable;

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

    public int getFaithMarker() {
        return faithMarker;
    }

    public PopeFavorTiles getPopeFavorTiles(int index) {
        return popeFavorTiles[index];
    }
}
