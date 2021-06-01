package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import java.io.Serializable;

public class SlotView implements Serializable {
    protected final Id id;
    protected final boolean confirmed;

    public SlotView(Id id, boolean confirmed){
        this.id = id;
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

}
