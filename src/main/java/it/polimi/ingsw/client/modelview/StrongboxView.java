package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.Strongbox;

import java.io.Serializable;

import static it.polimi.ingsw.server.model.ResourceType.*;
import static it.polimi.ingsw.server.model.ResourceType.SERVANT;

/**
 * View equivalent to the strongbox class.
 */
public class StrongboxView implements Serializable {

    private final Resource shield;
    private final Resource coin;
    private final Resource stone;
    private final Resource servant;

    /**
     * Constructor used at the start of a game. Sets all resources to 0.
     */
    public StrongboxView(){
        this.shield = new Resource(0, SHIELD);
        this.coin = new Resource(0, COIN);
        this.stone = new Resource(0, STONE);
        this.servant = new Resource(0, SERVANT);

    }

    /**
     * Constructor used to recreate the view from an existing strongbox.
     * @param strongbox the Strongbox to be copied.
     */
    public  StrongboxView(Strongbox strongbox) {
        Resource [] resources = strongbox.getAvailableResources();
        shield = resources[0];
        coin = resources[1];
        stone = resources[2];
        servant = resources[3];
    }

    public Resource getShield() {
        return shield;
    }

    public Resource getCoin() {
        return coin;
    }

    public Resource getStone() {
        return stone;
    }

    public Resource getServant() {
        return servant;
    }
}
