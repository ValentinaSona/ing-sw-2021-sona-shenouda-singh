package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotSufficientResourceException;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;

import java.util.ArrayList;
import java.util.Map;

public abstract class Slot {
    protected final Id id;
    protected Map<Id, Resource> originResourceHashMap;
    protected final ArrayList<Resource> resourceCloset = new ArrayList<>();
    protected boolean confirmed;

    public Slot(Id id){
        this.id = id;
    }

    public void setResourceCloset(Map<Id,Resource> idResourceMap) {
        //the map make it possible to understand from where we are taking the resource
        //for buying or activating a developmentCard
        this.originResourceHashMap = idResourceMap;

        for(Id id : idResourceMap.keySet()){
            Resource  resource = idResourceMap.get(id);
            boolean found = false;
            for (Resource res : resourceCloset) {

                if (res.getResourceType() == resource.getResourceType()) {
                    res.add(resource);
                    found = true;
                    break;
                }
            }
            if (!found) resourceCloset.add(resource);
        }
    }

    public abstract void check(boolean card) throws NotSufficientResourceException;

    public boolean isConfirmed() {
        return confirmed;
    }

    public abstract Resource[] activateProduction();
    public abstract Resource[] productionCost();


    public Id getId() { return id;}
}