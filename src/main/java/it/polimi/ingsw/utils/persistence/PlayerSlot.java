package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Slot;

import java.util.List;

public class PlayerSlot {

    private String nickname;
    private List<Slot> slots;

    public PlayerSlot (Player player) {
        nickname = player.getNickname();
        slots = player.getSlots();
    }

    public String getNickname() {
        return nickname;
    }

    public List<Slot> getSlots() {
        return slots;
    }
}
