package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

public interface LeaderCardSelectionController extends UiControllerInterface {
    public void handleSetupUserMessage(ServerSetupUserMessage message);
    public void handleSetupActionMessage(ServerSetupActionMessage message);
}
