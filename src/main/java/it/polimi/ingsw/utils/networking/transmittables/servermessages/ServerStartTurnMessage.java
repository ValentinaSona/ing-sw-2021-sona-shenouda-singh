package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerStartTurnMessage implements ServerMessage, ClientHandleable {
    private final User endingTurn;
    private final User startingTurn;

    public ServerStartTurnMessage(User startingTurn, User endingTurn){
        this.startingTurn = startingTurn;
        this.endingTurn = endingTurn;
    }

    public User getEndingTurn() {
        return endingTurn;
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
