package it.polimi.ingsw.server.model.action;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;

public interface Action {
    public default void handleDisconnection(Player player, Controller controller, RemoteViewHandler view, User user){}
    public default void handleReconnection(Player player, ServerGameReconnectionMessage message){}
}
