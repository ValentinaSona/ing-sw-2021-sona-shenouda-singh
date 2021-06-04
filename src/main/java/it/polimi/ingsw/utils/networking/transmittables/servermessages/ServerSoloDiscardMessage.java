package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.model.DevelopmentType;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerSoloDiscardMessage  implements ServerMessage, ClientHandleable {
    private final DevMarketView devView;
    private final DevelopmentType type;

    public ServerSoloDiscardMessage(DevMarketView devView, DevelopmentType type){
        this.devView = devView;
        this.type = type;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleSoloDiscard(this);
        return true;
    }

    public DevMarketView getDevView() {
        return devView;
    }

    public DevelopmentType getType() {
        return type;
    }
}
