package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientBuyTargetCardMessage  implements ClientMessage, ControllerHandleable {
    private final Id slotId;

    public ClientBuyTargetCardMessage(Id slotId){
        this.slotId = slotId;
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        handler.developmentCardMarketController.buyTargetCard(this, view, user);
        return  true;
    }

    public Id getSlotId() {
        return slotId;
    }
}
