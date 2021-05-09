package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotSufficientResourceException;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Slot extends AbstractModel {
    protected final Id id;
    protected final HashMap<Id, Resource> originResourceHashMap = new HashMap<>();
    protected final ArrayList<Resource> resourceCloset = new ArrayList<>();
    protected boolean confirmed;

    public Slot(Id id){
        this.id = id;
    }

    public void setResourceCloset(Resource resource, Id id) {
        //the map make it possible to understand from where we are taking the resource
        //for buying or activating a developmentCard
        this.originResourceHashMap.put(id, resource);

        for (Resource res : resourceCloset) {
            if (res.getResourceType() == resource.getResourceType()) {
                res.add(resource);
                return;
            }
        }
        resourceCloset.add(resource);
    }

    public abstract void check(boolean card) throws NotSufficientResourceException;

    public boolean isConfirmed() {
        return confirmed;
    }

    public abstract Resource[] activateProduction();
}