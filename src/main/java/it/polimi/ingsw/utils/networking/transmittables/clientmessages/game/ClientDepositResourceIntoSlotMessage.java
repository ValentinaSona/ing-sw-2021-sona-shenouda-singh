package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientDepositResourceIntoSlotMessage implements ClientMessage, ControllerHandleable {
    private final Id slotId;
    private final Resource resource;
    private final Id resourceId;

    public ClientDepositResourceIntoSlotMessage(Id slotId, Resource resource, Id resourceId){
        this.slotId = slotId;
        this.resource = resource;
        this.resourceId = resourceId;
    }
    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.resourceController.depositResourceIntoSlot(this, view, user);
        return  true;
    }

    public Id getSlotId() {
        return slotId;
    }

    public Resource getResource() {
        return resource;
    }

    public Id getResourceId() {
        return resourceId;
    }
}
