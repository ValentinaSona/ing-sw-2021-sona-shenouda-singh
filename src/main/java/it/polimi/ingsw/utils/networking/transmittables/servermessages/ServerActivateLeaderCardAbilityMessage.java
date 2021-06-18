package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.SlotView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerActivateLeaderCardAbilityMessage implements ServerMessage, ClientHandleable {
    private final LeaderCard ability;
    private final User user;
    private final ArrayList<DepotView> warehouse;
    private final ArrayList<SlotView> slots;

    public ServerActivateLeaderCardAbilityMessage(LeaderCard targetCard, ArrayList<DepotView> warehouse, ArrayList<SlotView> slots, User user) {
        this.ability = targetCard;
        this.user = user;
        this.warehouse = warehouse;
        this.slots = slots;
    }

    public User getUser() {
        return user;
    }

    public LeaderCard getAbility() {
        return ability;
    }

    public ArrayList<DepotView> getWarehouse() {
        return warehouse;
    }

    public ArrayList<SlotView> getSlots() {
        return slots;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleActivateLeaderCardAbility(this);
        return true;
    }
}
