package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepotTest {

    @Test
    void setResource() {
        Resource tooMuch = new Resource(6, ResourceType.STONE);
        Resource invalidRes = new Resource(6, ResourceType.FAITH);
        Resource resource = new Resource(2, ResourceType.SERVANT);
        Resource emptyResource = new Resource(0, ResourceType.SERVANT);
        Resource differentResource = new Resource(2, ResourceType.STONE);

        Depot depot = new Depot(4);

        try {
            depot.setResource(invalidRes);
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }

        try {
            depot.setResource(tooMuch);
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }

        depot.setResource(resource);
        assertEquals(resource, depot.getResource());

        depot.setNullIfEmpty();
        assertEquals(resource, depot.getResource());

        try {
            depot.setResource(differentResource);
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }

        depot.setResource(emptyResource);
        depot.setNullIfEmpty();
        assertNull(depot.getResource());




    }
}