package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
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


    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleStartTurn(this);
        return true;
    }
}
