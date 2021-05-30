package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerStartSetupTurnMessage implements ServerMessage, ClientHandleable {
    private final User startingTurn;

    public ServerStartSetupTurnMessage(User startingTurn){
        this.startingTurn = startingTurn;
    }

    public User getStartingTurn() {
        return startingTurn;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
