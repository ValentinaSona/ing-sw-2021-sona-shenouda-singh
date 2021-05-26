package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.SlotView;
import it.polimi.ingsw.client.modelview.StrongboxView;
import it.polimi.ingsw.client.modelview.WarehouseView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerDepositIntoSlotMessage implements ServerMessage, ClientHandleable {
    private final WarehouseView warehouseView;
    private final StrongboxView strongBoxView;
    private final ArrayList<SlotView> slotViews;
    private final User user;

    public ServerDepositIntoSlotMessage(WarehouseView warehouseView, StrongboxView strongBoxView, ArrayList<SlotView> slotViews, User user){
        this.slotViews = slotViews;
        this.strongBoxView = strongBoxView;
        this.warehouseView = warehouseView;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public WarehouseView getWarehouseView() {
        return warehouseView;
    }

    public ArrayList<SlotView> getSlotViews() {
        return slotViews;
    }

    public StrongboxView getStrongBoxView() {
        return strongBoxView;
    }

    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
