package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

public interface UiControllerInterface {
    public void handleStatusMessage(StatusMessage message);
}
