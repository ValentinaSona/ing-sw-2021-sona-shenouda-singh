package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

public interface UiControllerInterface {
    void handleStatusMessage(StatusMessage message);
}
