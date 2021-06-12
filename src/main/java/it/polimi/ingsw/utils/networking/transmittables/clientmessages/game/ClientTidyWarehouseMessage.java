package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientTidyWarehouseMessage implements ClientMessage, ControllerHandleable {
    private final Id from;
    private final Id to;
    public ClientTidyWarehouseMessage (Id from, Id to){
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.resourceController.tidyWarehouse(this, view, user);
        return  true;
    }

    public Id getFrom() {
        return from;
    }

    public Id getTo() {
        return to;
    }
}
