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
     * Response message if the selected object is not present (e.g. empty deck).
     */
    EMPTY_ERROR,
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

    /**
     * Response message sent by the server to the first player if the count has been set successfully
     */
    OK_COUNT,
    /**
     * Response message if the command could not be executed due to an unexpected server error.
     */
    SERVER_ERROR,

    /**
     * Response message sent by the server to the first player
     */
    SET_COUNT,

    /**
     * Response message sent by the server if the player has joined the lobby
     */
    JOIN_LOBBY,

    /**
     * Response message sent by the server if the player reconnected successfully
     */
    RECONNECTION_OK,

    /**
     * Response message sent by the server if the player failed to reconnect
     */
    RECONNECTION_FAILED,

    /**
     * Response message sent by the server if the request of the first player to load the game from file
     * has been dealt with success
     */
    LOAD_GAME_OK;
}
