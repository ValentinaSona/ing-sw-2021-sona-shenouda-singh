package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.server.ConnectionSetupHandler;


/**
 * All messages that implements this interface are processed by the
 * conncetionSetupHandler when the player is trying to join the lobby
 */
public interface ServerHandleable {
    /**
     *
     * @param handler
     * @return
     */
    boolean handleMessage(ConnectionSetupHandler handler);

}
