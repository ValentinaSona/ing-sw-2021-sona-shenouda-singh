package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerSoloDiscardMessage  implements ServerMessage, ClientHandleable {
    private final DevMarketView devView;

    public ServerSoloDiscardMessage(DevMarketView devView){
        this.devView = devView;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleSoloDiscard(this);
        return true;
    }

    public DevMarketView getDevView() {
        return devView;
    }
}
