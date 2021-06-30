package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerLastTurnsMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<User> lastUsers;
    private final EndOfGameCause cause;

    public ServerLastTurnsMessage(ArrayList<User> lastUsers, EndOfGameCause cause) {
        this.lastUsers = lastUsers;
        this.cause = cause;
    }

    public ArrayList<User> getLastUsers() {
        return lastUsers;
    }

    public EndOfGameCause getCause() {
        return cause;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleLastTurns(this);
        return true;
    }

}
