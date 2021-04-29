package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongboxTest {

    @Test
    void getAvailableResources() {
        Strongbox strongbox = new Strongbox();
        Resource[] voidRes = new Resource [] {new Resource(0, ResourceType.SHIELD), new Resource(0, ResourceType.COIN), new Resource(0, ResourceType.STONE), new Resource(0, ResourceType.SERVANT)};
        assertArrayEquals(voidRes, strongbox.getAvailableResources());

        Strongbox strongboxFull = new Strongbox(new Resource(5, ResourceType.SHIELD), new Resource(7, ResourceType.COIN), new Resource(8, ResourceType.STONE), new Resource(9, ResourceType.SERVANT));
        Resource[] newRes = new Resource [] {new Resource(5, ResourceType.SHIELD), new Resource(7, ResourceType.COIN), new Resource(8, ResourceType.STONE), new Resource(9, ResourceType.SERVANT)};
        assertArrayEquals(newRes, strongboxFull.getAvailableResources());
    }

    @Test
    void addResources() {
        Strongbox strongbox = new Strongbox(new Resource(5, ResourceType.SHIELD), new Resource(7, ResourceType.COIN), new Resource(8, ResourceType.STONE), new Resource(9, ResourceType.SERVANT));
        strongbox.addResources(new Resource[]{new Resource(1, ResourceType.SHIELD), new Resource(2, ResourceType.COIN), new Resource(3, ResourceType.STONE), new Resource(4, ResourceType.SERVANT)});

        Resource[] sumRes = new Resource[] {new Resource(6, ResourceType.SHIELD), new Resource(9, ResourceType.COIN), new Resource(11, ResourceType.STONE), new Resource(13, ResourceType.SERVANT)};
        assertArrayEquals(sumRes, strongbox.getAvailableResources());

        try {
            strongbox.addResources(new Resource(4, ResourceType.FAITH));
            Assertions.fail();
        } catch (RuntimeException e) {
            Assertions.assertTrue(true);
        }
    }
}