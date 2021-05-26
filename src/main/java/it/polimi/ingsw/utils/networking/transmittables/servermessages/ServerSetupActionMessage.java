package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.WarehouseView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerSetupActionMessage implements ServerMessage, ClientHandleable {
    private final WarehouseView warehouseView;
    private final User user;
    private final LeaderCard[] chosen;

    public ServerSetupActionMessage(WarehouseView warehouseView, LeaderCard[] chosen, User user){
        this.chosen = chosen;
        this.user = user;
        this.warehouseView = warehouseView;
    }

    public LeaderCard[] getChosen() {
        return chosen;
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
