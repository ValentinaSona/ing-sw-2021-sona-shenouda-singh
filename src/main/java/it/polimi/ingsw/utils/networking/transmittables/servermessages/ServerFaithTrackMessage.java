package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.FaithTrackView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerFaithTrackMessage  implements ServerMessage, ClientHandleable {
    private final FaithTrackView faithTrackView;
    private final User user;

    public ServerFaithTrackMessage(FaithTrackView faithTrackView, User user){
        this.faithTrackView = faithTrackView;
        this.user = user;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleFaithTrackMessage(this);
        return true;
    }
}
