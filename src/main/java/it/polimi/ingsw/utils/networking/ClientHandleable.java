package it.polimi.ingsw.utils.networking;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;

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
    boolean handleMessage(UiControllerInterface handler);
}
