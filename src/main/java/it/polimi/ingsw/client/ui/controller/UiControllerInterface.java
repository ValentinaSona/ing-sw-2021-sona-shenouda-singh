package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

/**
 * Interface for the UIController. Is used by the main UIController class and my multiple GUIControllers.
 */
public interface UiControllerInterface {
    void handleStatusMessage(StatusMessage message);
}
