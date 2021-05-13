package it.polimi.ingsw.utils.networking.transmittables;

import it.polimi.ingsw.utils.networking.Transmittable;
/**
 * An enum to contain all possible messages that can be sent from the Controller to the View
 */

public enum StatusMessage implements Transmittable {
    /**
     * Response message if the command could not be executed due to a bad request from the client.
     */
    CLIENT_ERROR,
    /**
     * Response message if the command succeeded, but the other party needs additional information to complete it.
     */
    CONTINUE,
    /**
     * Response message if the command has been executed successfully.
     */
    OK,
    /**
     * Response message if the command could not be executed due to an unexpected server error.
     */
    SERVER_ERROR;
}
