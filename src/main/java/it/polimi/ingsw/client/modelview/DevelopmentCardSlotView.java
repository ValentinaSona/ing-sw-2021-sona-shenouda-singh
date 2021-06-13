package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Id;

import java.io.Serializable;
import java.util.Stack;

public class DevelopmentCardSlotView extends SlotView implements Serializable {
    private final Stack<DevelopmentCard> slot;

    public DevelopmentCardSlotView(Id slot1) {
        super(slot1, false);
        slot = new Stack<>();
    }

    public DevelopmentCardSlotView(Id slot1, Stack<DevelopmentCard> slot) {
        super(slot1, false);
        this.slot = slot;
    }
    public DevelopmentCard peek(){
        if (!slot.empty()) return slot.peek();
        else return null;
    }
}
