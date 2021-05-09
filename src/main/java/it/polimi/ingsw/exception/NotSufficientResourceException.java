package it.polimi.ingsw.exception;

import it.polimi.ingsw.model.Id;
import it.polimi.ingsw.model.Resource;

import java.util.HashMap;

public class NotSufficientResourceException extends Exception {
    private final HashMap<Id, Resource> tempResources;
    public NotSufficientResourceException(HashMap<Id, Resource> tempResources) {
        super();
        this.tempResources =tempResources;
    }

    public HashMap<Id, Resource> getTempResources(){
        return tempResources;
    }
}
