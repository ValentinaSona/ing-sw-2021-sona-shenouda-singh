package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import java.io.Serializable;


public class SpecialProductionView extends SlotView implements Serializable {

    public SpecialProductionView(Id id, boolean confirmed) {
        super(id, confirmed);
    }
}
