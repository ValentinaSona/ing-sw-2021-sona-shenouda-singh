package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

import java.util.Map;

public class ClientSetupActionMessage implements ClientMessage, ControllerHandleable {
    //where to put the initial resources
    private final Map<Id, Resource> idResourceMap;
    private final LeaderCard[] chosen;
    private final User user;

    public ClientSetupActionMessage(Map<Id,Resource> idResourceMap, LeaderCard[] chosen, User user){
        this.chosen = chosen;
        this.idResourceMap = idResourceMap;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public LeaderCard[] getChosen() {
        return chosen;
    }

    public Map<Id, Resource> getIdResourceMap() {
        return idResourceMap;
    }


    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        handler.setupAction(this, view,user);
        return false;
    }
}
