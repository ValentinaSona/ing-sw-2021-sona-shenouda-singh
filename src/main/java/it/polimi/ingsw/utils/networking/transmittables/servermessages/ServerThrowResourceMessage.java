package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerThrowResourceMessage implements ServerMessage, ClientHandleable {
    private final User user;
    private final Integer thrownResources;

    public ServerThrowResourceMessage(Integer thrownResources, User user){
        this.user = user;
        this.thrownResources = thrownResources;
    }

    public User getUser() {
        return user;
    }

    public Integer getThrownResources() {
        return thrownResources;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleThrowResource(this);
        return true;
    }
}
