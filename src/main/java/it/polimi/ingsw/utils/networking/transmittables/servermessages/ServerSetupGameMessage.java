package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerSetupGameMessage implements ServerMessage, ClientHandleable {
    private final ArrayList<User> users;

    public ServerSetupGameMessage(ArrayList<User> users){
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    //TODO
    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
