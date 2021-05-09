package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;

public class DepotView {
    final private Id id;
    final private Resource resource;
    final private int capacity;

    public DepotView(Id id, Resource resource, int capacity){
        this.id = id;
        this.resource = resource;
        this.capacity = capacity;
    }

    public Id getId(){
        return id;
    }
    public Resource getResource() {
        return new Resource(resource.getQuantity(), resource.getResourceType());
    }

    public int getCapacity() {
        return capacity;
    }
}
