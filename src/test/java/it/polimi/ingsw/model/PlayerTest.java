package it.polimi.ingsw.model;

import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.*;
import org.junit.jupiter.api.Test;

class PlayerTest {
/*
    @Test
    void canYouActivate() {
        LeaderCard[] cards= new LeaderCard[]{
                new LeaderCard(new Requirement[]{
                        new Requirement(1,2, DevelopmentType.GREEN),
                        new Requirement(2,0,DevelopmentType.BLUE)
                },1,new SpecialAbility()),
                new LeaderCard(new Requirement[]{
                        new Requirement(new Resource(5, ResourceType.STONE))
                }, 1, new SpecialAbility())
        };

        Player player = new Player("1,2,cards");

        assertFalse(player.canYouActivate(player.getLeaderCards().get(0)));
        assertFalse(player.canYouActivate(player.getLeaderCards().get(1)));

        player.getStrongbox().addResources(new Resource(3,ResourceType.STONE));
        assertFalse(player.canYouActivate(player.getLeaderCards()[1]));

        try {
            player.getWarehouse().get(2).addResource(new Resource(2,ResourceType.STONE));
        } catch (InvalidDepotException e) {
            e.printStackTrace();
        }
        assertTrue(player.canYouActivate(player.getLeaderCards()[1]));

        DevelopmentCard blue1 = new DevelopmentCard(0,new Resource[]{new Resource(1,ResourceType.STONE)},DevelopmentType.BLUE,1,1,new Production(new Resource[]{new Resource(1,ResourceType.STONE)}, new Resource[]{new Resource(1,ResourceType.STONE)}));
        DevelopmentCard blue2 = new DevelopmentCard(1,new Resource[]{new Resource(1,ResourceType.STONE)},DevelopmentType.BLUE,2,1,new Production(new Resource[]{new Resource(1,ResourceType.STONE)}, new Resource[]{new Resource(1,ResourceType.STONE)}));
        DevelopmentCard green = new DevelopmentCard(2,new Resource[]{new Resource(1,ResourceType.STONE)},DevelopmentType.GREEN,2,1,new Production(new Resource[]{new Resource(1,ResourceType.STONE)}, new Resource[]{new Resource(1,ResourceType.STONE)}));

        player.getDevelopmentCardSlots()[0].push(blue1);
        assertFalse(player.canYouActivate(player.getLeaderCards()[0]));
        player.getDevelopmentCardSlots()[0].push(blue2);
        assertFalse(player.canYouActivate(player.getLeaderCards()[0]));
        player.getDevelopmentCardSlots()[0].push(green);
        assertTrue(player.canYouActivate(player.getLeaderCards()[0]));
    }*/


}