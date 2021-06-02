package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;



public interface LobbyMenuController extends UiControllerInterface{
    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message);
    public void handleSetupGameMessage(ServerSetupGameMessage message);
}
