package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ResourceTest {

    @Test
    public void getQuantity() {
        Resource resource = new Resource(2, ResourceType.STONE);
        assertEquals(2, resource.getQuantity());
    }

    @Test
    void getResourceType() {
        Resource resource = new Resource(2, ResourceType.SERVANT);
        assertEquals(ResourceType.SERVANT, resource.getResourceType());
    }

    @Test
    void add() {
        Resource resource1 = new Resource(2, ResourceType.SERVANT);
        Resource resource2 = new Resource(4, ResourceType.SERVANT);

        resource1.add(resource2);

        assertEquals(6, resource1.getQuantity());
        assertEquals(ResourceType.SERVANT, resource1.getResourceType());

    }

    @Test
    void addDiff() {
        Resource resource1 = new Resource(2, ResourceType.SERVANT);
        Resource resource2 = new Resource(4, ResourceType.COIN);

        try {
            resource1.add(resource2);
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }


    }
}