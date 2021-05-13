package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientDepositIntoWarehouseMessage implements ClientMessage, ControllerHandleable {
    private final Id slotId;
    private final Resource resource;

    public ClientDepositIntoWarehouseMessage(Id slotId, Resource resource){
        this.slotId = slotId;
        this.resource = resource;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.resourceController.depositIntoWarehouse(this, view, user);
        return  true;
    }

    public Id getSlotId() {
        return slotId;
    }

    public Resource getResource() {
        return resource;
    }
}