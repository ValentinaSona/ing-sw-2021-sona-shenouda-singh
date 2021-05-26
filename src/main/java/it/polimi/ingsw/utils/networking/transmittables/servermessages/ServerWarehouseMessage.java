package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.WarehouseView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerWarehouseMessage implements ServerMessage, ClientHandleable {
    private final WarehouseView warehouseView;
    private final User user;

    public ServerWarehouseMessage(WarehouseView warehouseView, User user){
        this.warehouseView = warehouseView;
        this.user =user;
    }

    public User getUser() {
        return user;
    }

    public WarehouseView getWarehouseView() {
        return warehouseView;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
