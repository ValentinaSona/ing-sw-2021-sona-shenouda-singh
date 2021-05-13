package it.polimi.ingsw.utils.networking;
import it.polimi.ingsw.client.modelview.Client;
/**
 * All messages that implements this interface are processed by the
 * client method dispatchServerMessages
 */
public interface ClientHandleable {
    /** TODO: actual client implementation
     * Every class that represent a message implements this method calling a different
     * method of the client
     * @param handler
     * @return
     */
    boolean handleMessage(Client handler);
}
