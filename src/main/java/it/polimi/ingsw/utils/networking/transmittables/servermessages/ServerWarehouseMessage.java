package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerWarehouseMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<DepotView> warehouseView;
    private final User user;

    public ServerWarehouseMessage(ArrayList<DepotView>  warehouseView, User user){
        this.warehouseView = warehouseView;
        this.user =user;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<DepotView>  getWarehouseView() {
        return warehouseView;
    }

    //TODO
    @Override
    public boolean handleMessage(DispatcherController handler) {
        return false;
    }
}
