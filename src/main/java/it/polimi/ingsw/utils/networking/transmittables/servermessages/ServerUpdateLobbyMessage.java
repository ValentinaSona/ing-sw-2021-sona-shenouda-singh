package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.LobbyMenuController;
import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerUpdateLobbyMessage implements ServerMessage, ClientHandleable {
    private ArrayList<User> lobbyUsers;

    public ServerUpdateLobbyMessage(ArrayList<User> lobbyUsers){
        this.lobbyUsers = lobbyUsers;
    }

    public ArrayList<User> getLobbyUsers() {
        return lobbyUsers;
    }


    @Override
    public boolean handleMessage(UiControllerInterface handler) {
        try{
            ((LobbyMenuController) handler).handleUpdateLobbyMessage(this);
            return true;
        }catch (ClassCastException e){
            //this should never happen
            e.printStackTrace();
            return false;
        }
    }
}
