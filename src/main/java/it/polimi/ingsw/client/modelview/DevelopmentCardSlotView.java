package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Id;

import java.io.Serializable;
import java.util.Stack;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

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

    public int hiddenVP(){
        int vp = 0;
        if (slot == null || slot.isEmpty()) return 0;
        for (DevelopmentCard card : slot){
            if (card != slot.peek()) vp += card.getVictoryPoints();
        }
        return vp;
    }

    public String hiddenColors(){
        StringBuilder colors = new StringBuilder();
        colors.append("");
        if (slot == null || slot.isEmpty()) return colors.toString();
        for (DevelopmentCard card : slot){
            if (card == slot.peek()) continue;
            switch (card.getType()){
                case PURPLE -> colors.append(" " + ANSI_PURPLE + SQUARE + ANSI_RESET);
                case BLUE -> colors.append(" " +ANSI_BLUE+ SQUARE + ANSI_RESET);
                case GREEN -> colors.append(" " +ANSI_GREEN + SQUARE + ANSI_RESET);
                case YELLOW -> colors.append(" " +ANSI_YELLOW + SQUARE + ANSI_RESET);
            }
        }
        return colors.toString();
    }
}
