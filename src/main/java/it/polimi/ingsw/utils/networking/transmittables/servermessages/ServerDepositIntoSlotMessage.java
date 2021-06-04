package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.SlotView;
import it.polimi.ingsw.client.modelview.StrongboxView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerDepositIntoSlotMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<DepotView> warehouseView;
    private final StrongboxView strongBoxView;
    private final ArrayList<SlotView> slotViews;
    private final User user;

    public ServerDepositIntoSlotMessage(ArrayList<DepotView> warehouseView, StrongboxView strongBoxView, ArrayList<SlotView> slotViews, User user){
        this.slotViews = slotViews;
        this.strongBoxView = strongBoxView;
        this.warehouseView = warehouseView;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<DepotView> getWarehouseView() {
        return warehouseView;
    }

    public ArrayList<SlotView> getSlotViews() {
        return slotViews;
    }

    public StrongboxView getStrongBoxView() {
        return strongBoxView;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleDepositIntoSlot(this);
        return true;
    }
}
