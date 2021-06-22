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
     * Response message if the command could not be executed due to an unmet requirement.
     */
    REQUIREMENTS_ERROR,
    /**
     * Response message if the command could not be executed but a different selection might remove the error.
     */
    SELECTION_ERROR,
    /**
     * Response message if the command succeeded, but the other party needs additional information to complete it.
     */
    CONTINUE,
    /**
     * Response message if the command is asking needs additional information to handle an exception.
     */
    CONTINUE_EXCEPTION,
    /**
     * Response message if the command has been executed successfully.
     */
    OK_NICK,

    OK_COUNT,
    /**
     * Response message if the command could not be executed due to an unexpected server error.
     */
    SERVER_ERROR,

    SET_COUNT,

    JOIN_LOBBY,

    RECONNECTION_OK;
}
