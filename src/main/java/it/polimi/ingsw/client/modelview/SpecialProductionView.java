package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;

import java.io.Serializable;

/**
 * View equivalent to special production. Is added to the player slot list.
 */
public class SpecialProductionView extends SlotView implements Serializable {
    private final Production specialProduction;

    /**
     * Initializes a special production.
     * @param id id of the slot.
     * @param costType the type of resource that the special production requires to be activated. It's used to create the production.
     */
    public SpecialProductionView(Id id, ResourceType costType) {
        super(id);
        Resource[] cost = new Resource[]{new Resource(1, costType)};
        Resource[] out = new Resource[]{new Resource(1, ResourceType.JOLLY), new Resource(1, ResourceType.FAITH)};
        this.specialProduction = new Production(cost, out);
    }

    public Production getSpecialProduction() {
        return specialProduction;
    }
}
