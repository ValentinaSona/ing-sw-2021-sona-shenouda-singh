package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;
import java.util.List;

public class ServerSetupActionMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<DepotView> warehouseView;
    private final User user;
    private final List<LeaderCard> chosen;

    public ServerSetupActionMessage(ArrayList<DepotView> warehouseView, List<LeaderCard> chosen, User user){
        this.chosen = chosen;
        this.user = user;
        this.warehouseView = warehouseView;
    }

    public List<LeaderCard> getChosen() {
        return chosen;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<DepotView> getWarehouseView() {
        return warehouseView;
    }

    //TODO

    @Override
    public boolean handleMessage(UiControllerInterface handler) {
        return false;
    }
}
