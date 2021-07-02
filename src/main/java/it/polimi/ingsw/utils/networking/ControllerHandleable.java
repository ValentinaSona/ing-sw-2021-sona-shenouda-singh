package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.view.RemoteViewHandler;

/**
 * All messages that implements this interface are processed by the
 * controller method dispatchClientMessages
 */
public interface ControllerHandleable {
    /**
     * Every class that represent a message implements this method calling a different
     * controller method
     * @param handler reference to the controller that will handle this message
     * @param view remote view of the player that sent the message
     * @param user user that represent the player at network layer
     * @return true if there where no errors while processing this message
     */
    boolean handleMessage(Controller handler, RemoteViewHandler view, User user);

}
