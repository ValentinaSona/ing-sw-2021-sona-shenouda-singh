package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Production;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;

import java.io.Serializable;


public class SpecialProductionView extends SlotView implements Serializable {
    private Production specialProduction;

    public SpecialProductionView(Id id, boolean confirmed, ResourceType chosenType) {
        super(id, confirmed);
        Resource[] cost = new Resource[]{new Resource(1, chosenType)};
        Resource[] out = new Resource[]{new Resource(1, ResourceType.JOLLY), new Resource(1, ResourceType.FAITH)};
        this.specialProduction = new Production(cost, out);
    }

    public Production getSpecialProduction() {
        return specialProduction;
    }
}
