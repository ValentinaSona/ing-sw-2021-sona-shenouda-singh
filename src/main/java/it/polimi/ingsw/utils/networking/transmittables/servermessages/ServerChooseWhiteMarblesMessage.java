package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerChooseWhiteMarblesMessage implements ServerMessage, ClientHandleable {
    private final Integer whiteMarbles;
    private final User user;

    public ServerChooseWhiteMarblesMessage(Integer whiteMarbles, User user){
        this.whiteMarbles = whiteMarbles;
        this.user = user;
    }

    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
