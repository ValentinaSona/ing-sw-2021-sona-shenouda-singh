package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Resource;

import java.io.Serializable;

import static it.polimi.ingsw.server.model.ResourceType.*;
import static it.polimi.ingsw.server.model.ResourceType.SERVANT;

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
