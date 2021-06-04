package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerSoloMoveMessage  implements ServerMessage, ClientHandleable {
    private final int blackCross;
    private final boolean shuffled;

    public ServerSoloMoveMessage(int blackCross, boolean shuffled){
        this.blackCross = blackCross;
        this.shuffled = shuffled;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleSoloMove(this);
        return true;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public boolean isShuffled() {
        return shuffled;
    }
}