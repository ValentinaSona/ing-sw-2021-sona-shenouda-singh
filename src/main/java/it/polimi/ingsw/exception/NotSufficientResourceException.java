package it.polimi.ingsw.exception;

import it.polimi.ingsw.model.Origin;
import it.polimi.ingsw.model.Resource;

import java.util.HashMap;

public class NotSufficientResourceException extends Exception {
    private final HashMap<Origin, Resource> tempResources;
    public NotSufficientResourceException(HashMap<Origin, Resource> tempResources) {
        super();
        this.tempResources =tempResources;
    }

    public HashMap<Origin, Resource> getTempResources(){
        return tempResources;
    }
}
