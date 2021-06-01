package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
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

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
