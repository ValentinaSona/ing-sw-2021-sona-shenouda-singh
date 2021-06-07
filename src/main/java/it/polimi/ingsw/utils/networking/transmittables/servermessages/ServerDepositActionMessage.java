package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;
import java.util.List;

public class ServerDepositActionMessage implements ServerMessage, ClientHandleable {
    private final List<Resource> tempResources;
    private final List<DepotView> warehouseView;
    private final User user;

    public ServerDepositActionMessage(List<Resource> tempResources, ArrayList<DepotView> warehouseView, User user){
        this.tempResources = tempResources;
        this.warehouseView = warehouseView;
        this.user = user;
    }

    public List<DepotView> getWarehouseView() {
        return warehouseView;
    }

    public User getUser() {
        return user;
    }

    public List<Resource> getTempResources() {
        return tempResources;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleDepositAction(this);
        return true;
    }
}
