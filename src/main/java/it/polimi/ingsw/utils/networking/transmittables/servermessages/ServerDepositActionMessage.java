package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.WarehouseView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerDepositActionMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<Resource> tempResources;
    private final WarehouseView warehouseView;
    private final User user;

    public ServerDepositActionMessage(ArrayList<Resource> tempResources, WarehouseView warehouseView, User user){
        this.tempResources = tempResources;
        this.warehouseView = warehouseView;
        this.user = user;
    }

    public WarehouseView getWarehouseView() {
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
