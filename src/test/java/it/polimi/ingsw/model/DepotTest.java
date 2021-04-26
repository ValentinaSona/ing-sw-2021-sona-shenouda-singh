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
        Resource emptyResource = new Resource(-2, ResourceType.SERVANT);
        Resource differentResource = new Resource(2, ResourceType.STONE);

        Depot depot = new Depot(4);

        try {
            depot.addResource(invalidRes);
            Assertions.fail();
        } catch (RuntimeException e) {
            assertNull(depot.getResource());
        }

        try {
            depot.addResource(tooMuch);
            Assertions.fail();
        } catch (RuntimeException e) {
            assertNull(depot.getResource());
        }

        depot.addResource(resource);
        assertEquals(resource, depot.getResource());


        try {
            depot.addResource(differentResource);
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }

        depot.addResource(emptyResource);
        assertNull(depot.getResource());




    }
}