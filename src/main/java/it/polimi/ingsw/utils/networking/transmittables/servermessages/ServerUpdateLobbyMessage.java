package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerUpdateLobbyMessage implements ServerMessage, ClientHandleable {
    private ArrayList<User> lobbyUsers;
    private int numOfPlayer;

    public ServerUpdateLobbyMessage(ArrayList<User> lobbyUsers, int numOfPlayer){
        this.numOfPlayer = numOfPlayer;
        this.lobbyUsers = lobbyUsers;
    }

    public ArrayList<User> getLobbyUsers() {
        return lobbyUsers;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleUpdateLobby(this);
        return true;
    }
}
