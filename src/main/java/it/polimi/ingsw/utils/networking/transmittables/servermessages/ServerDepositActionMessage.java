package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerDepositActionMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<Resource> tempResources;
    private final ArrayList<DepotView> warehouseView;
    private final User user;

    public ServerDepositActionMessage(ArrayList<Resource> tempResources, ArrayList<DepotView> warehouseView, User user){
        this.tempResources = tempResources;
        this.warehouseView = warehouseView;
        this.user = user;
    }

    public ArrayList<DepotView> getWarehouseView() {
        return warehouseView;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Resource> getTempResources() {
        return tempResources;
    }

    //TODO

    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
