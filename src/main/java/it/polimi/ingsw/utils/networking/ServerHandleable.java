package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.server.ConnectionSetupHandler;


/**
 * All messages that implements this interface are processed by the
 * ConnectionSetupHandler when the player is trying to join the lobby
 */
public interface ServerHandleable {
    /**
     *
     * @param handler ConnectionSetupHandler is the only observer that is able to
     *                handle all messages that inherit from this interface
     * @return true if the operation was concluded successfully
     */
    boolean handleMessage(ConnectionSetupHandler handler);

}
