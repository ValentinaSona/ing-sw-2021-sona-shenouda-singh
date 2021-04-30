package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.VaticanReportException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaithTrackTest {

    @Test
    void addFaithPoints() {
        FaithTrack faithTrack = new FaithTrack();
        Resource resource = new Resource(6, ResourceType.FAITH);
        Resource invalidResource = new Resource(6, ResourceType.SHIELD);
        try {
            try {
                faithTrack.addFaithPoints(invalidResource);
            } catch (VaticanReportException e) {
            }
            Assertions.fail();
        } catch (RuntimeException e) {
            assertEquals(faithTrack.getFaithMarker(), 0);
        }

        try {
            faithTrack.addFaithPoints(resource);
        } catch (VaticanReportException e) {

        }
        assertEquals( 6,faithTrack.getFaithMarker());

    }

    @Test
    void validatePopeFavor() {
        FaithTrack faithTrack = new FaithTrack();

        try {
            faithTrack.addFaithPoints(2);
        } catch (VaticanReportException e) {

        }
        faithTrack.validatePopeFavor("1");
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));


        try {
            faithTrack.addFaithPoints(6);
        } catch (VaticanReportException e) {
        }
        faithTrack.validatePopeFavor("2");
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));

        try {
            faithTrack.addFaithPoints(11);
        } catch (VaticanReportException e) {

        }
        faithTrack.validatePopeFavor("3");
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(2));
    }
}