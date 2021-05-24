package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.VaticanReportException;
import it.polimi.ingsw.server.model.observable.FaithTrack;
import it.polimi.ingsw.server.model.PopeFavorTiles;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
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
    void validatePopeFavor1() {
        FaithTrack faithTrack = new FaithTrack();

        try {
            faithTrack.addFaithPoints(2);
        } catch (VaticanReportException e) {
        //Player has two faith, someone else triggers vatican.
        }
        faithTrack.validatePopeFavor("1");
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));

        //check that a second call does not modify.
        faithTrack.validatePopeFavor("1");
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));

        try {
            faithTrack.addFaithPoints(6);
        } catch (VaticanReportException e) {
        }
        //player has eight faith,
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

        try {
            faithTrack.addFaithPoints(11);
        } catch (VaticanReportException e) {
            Assertions.fail();
        }

    }

    @Test
    void validatePopeFavor2() {
        FaithTrack faithTrack = new FaithTrack();

        try {
            faithTrack.validatePopeFavor("0");
            Assertions.fail();
        } catch (RuntimeException e) {

        }

        try {
            faithTrack.addFaithPoints(8);
        } catch (VaticanReportException e) {

            faithTrack.validatePopeFavor("1");
        }

        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));


        try {
            faithTrack.addFaithPoints(6);
        } catch (VaticanReportException e) {
        }
        //player has eight faith,
        faithTrack.validatePopeFavor("2");
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));

        try {
            faithTrack.addFaithPoints(1);
        } catch (VaticanReportException e) {

        }
        faithTrack.validatePopeFavor("3");
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DISMISSED,faithTrack.getPopeFavorTiles(2));

        try {
            faithTrack.addFaithPoints(11);
        } catch (VaticanReportException e) {
            Assertions.fail();
        }
    }

    @Test
    void validatePopeFavor3() {
        FaithTrack faithTrack = new FaithTrack();

        try {
            faithTrack.addFaithPoints(8);
        } catch (VaticanReportException e) {

            faithTrack.validatePopeFavor("1");
        }

        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));


        try {
            faithTrack.addFaithPoints(8);
        } catch (VaticanReportException e) {
        }
        //player has eight faith,
        faithTrack.validatePopeFavor("2");
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.DOWNWARDS,faithTrack.getPopeFavorTiles(2));

        try {
            faithTrack.addFaithPoints(16);
        } catch (VaticanReportException e) {

        }
        faithTrack.validatePopeFavor("3");
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(0));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(1));
        assertEquals(PopeFavorTiles.UPWARDS,faithTrack.getPopeFavorTiles(2));

    }
}