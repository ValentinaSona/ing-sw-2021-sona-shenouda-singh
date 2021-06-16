package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.StrongboxView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.ArrayList;

public class ServerActivateProductionMessage implements ServerMessage, ClientHandleable {
    private final StrongboxView strongboxView;
    private final User user;
    private final ArrayList<Resource> gained;
    private final ArrayList<Resource> spent;

    public ServerActivateProductionMessage(StrongboxView strongboxView, User user, ArrayList<Resource> gained, ArrayList<Resource> spent){
        this.strongboxView = strongboxView;
        this.user = user;
        this.gained = gained;
        this.spent = spent;
    }

    public StrongboxView getStrongboxView() {
        return strongboxView;
    }


    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleActivateProduction(this);
        return true;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<Resource> getGained() {
        return gained;
    }

    public ArrayList<Resource> getSpent() {
        return spent;
    }
}
