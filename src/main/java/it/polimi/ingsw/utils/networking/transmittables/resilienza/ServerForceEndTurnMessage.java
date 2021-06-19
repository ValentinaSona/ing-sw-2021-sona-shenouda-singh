package it.polimi.ingsw.utils.networking.transmittables.resilienza;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

public class ServerForceEndTurnMessage implements ServerMessage, ClientHandleable {
    private final User endingTurn;
    private final User startingTurn;

    public ServerForceEndTurnMessage(User startingTurn, User endingTurn) {
        this.startingTurn = startingTurn;
        this.endingTurn = endingTurn;
    }

    public User getEndingTurn() {
        return endingTurn;
    }

    public User getStartingTurn() {
        return startingTurn;
    }

    //TODO ...
    @Override
    public boolean handleMessage(DispatcherController handler) {

        return false;
    }
}
