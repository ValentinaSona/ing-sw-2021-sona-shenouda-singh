package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

import java.util.Map;

public class ClientDepositResourceIntoSlotMessage implements ClientMessage, ControllerHandleable {
    private final Id slotId;
    private final Map<Id, Resource> idResourceMap;
    private final Boolean card;
    private final ResourceType jollyType;

    public ClientDepositResourceIntoSlotMessage(Id slotId,  Map<Id, Resource> idResourceMap, ResourceType resourceType, Boolean card){
        this.slotId = slotId;
        this.idResourceMap =idResourceMap;
        this.card = card;
        this.jollyType = resourceType;
    }

    public ClientDepositResourceIntoSlotMessage(Id slotId,  Map<Id, Resource> idResourceMap){
        this.slotId = slotId;
        this.idResourceMap =idResourceMap;
        this.card = true;
        this.jollyType = null;
    }
    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.resourceController.depositResourceIntoSlot(this, view, user);
        return  true;
    }

    public Id getSlotId() {
        return slotId;
    }

    public Boolean isForCard() {
        return card;
    }

    public Map<Id, Resource> getIdResourceMap() {
        return idResourceMap;
    }

    public ResourceType getJollyType() {
        return jollyType;
    }

}
