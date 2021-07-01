package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import java.io.Serializable;

/**
 * View equivalent to the slot class.
 */
public class SlotView implements Serializable {
    protected final Id id;

    public SlotView(Id id){
        this.id = id;
    }

}
