package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.InvalidDepotException;
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

        Depot depot = new Depot(4, Id.DEPOT_1);

        try {
            depot.addResource(invalidRes);
            Assertions.fail();
        } catch (InvalidDepotException e) {
            assertNull(depot.getResource());
        }

        try {
            depot.addResource(tooMuch);
            Assertions.fail();
        } catch (InvalidDepotException e) {
            assertNull(depot.getResource());
        }

        try {
            depot.addResource(resource);
        } catch (InvalidDepotException e) {
            e.printStackTrace();
        }
        assertEquals(resource, depot.getResource());


        try {
            depot.addResource(differentResource);
            Assertions.fail();
        } catch (InvalidDepotException e) {
            Assertions.assertTrue(true);
        }

        try {
            depot.addResource(emptyResource);
        } catch (InvalidDepotException e) {
            e.printStackTrace();
        }
        assertNull(depot.getResource());




    }
}