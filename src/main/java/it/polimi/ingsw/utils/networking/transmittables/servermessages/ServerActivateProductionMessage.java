package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.client.modelview.StrongboxView;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerActivateProductionMessage implements ServerMessage, ClientHandleable {
    private final StrongboxView strongboxView;
    private final User user;

    public ServerActivateProductionMessage(StrongboxView strongboxView, User user){
        this.strongboxView = strongboxView;
        this.user = user;
    }

    public StrongboxView getStrongboxView() {
        return strongboxView;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
