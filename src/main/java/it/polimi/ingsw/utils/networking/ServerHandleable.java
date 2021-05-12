package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.server.Server;

/**
 * All messages that implements this interface are processed by the
 * server method dispatchClientMessages
 */
public interface ServerHandleable {
    /**
     *
     * @param server
     * @return
     */
    boolean handleMessage(Server server);

}
