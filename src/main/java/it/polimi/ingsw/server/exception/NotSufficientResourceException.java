package it.polimi.ingsw.server.exception;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;

import java.util.HashMap;

public class NotSufficientResourceException extends Exception {
    private final HashMap<Id, Resource> tempResources;
    public NotSufficientResourceException(HashMap<Id, Resource> tempResources) {
        super();
        this.tempResources =tempResources;
    }

    public NotSufficientResourceException(){
        super();
        tempResources = null;
    }

    public HashMap<Id, Resource> getTempResources(){
        return tempResources;
    }
}
