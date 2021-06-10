package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.FaithTrackView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerFaithTrackMessage  implements ServerMessage, ClientHandleable {
    private final FaithTrackView faithTrackView;
    private final User user;
    private final int faith;

    public ServerFaithTrackMessage(FaithTrackView faithTrackView, int faith, User user){
        this.faithTrackView = faithTrackView;
        this.user = user;
        this.faith = faith;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleFaithTrackMessage(this);
        return true;
    }

    public FaithTrackView getFaithTrackView() {
        return faithTrackView;
    }

    public User getUser() {
        return user;
    }

    public int getFaith() {
        return faith;
    }
}
