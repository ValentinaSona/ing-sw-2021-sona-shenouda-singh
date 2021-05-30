package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.modelview.FaithTrackView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerFaithTrackMessage  implements ServerMessage, ClientHandleable {
    private final FaithTrackView faithTrackView;
    private final User user;

    public ServerFaithTrackMessage(FaithTrackView faithTrackView, User user){
        this.faithTrackView = faithTrackView;
        this.user = user;
    }
    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
