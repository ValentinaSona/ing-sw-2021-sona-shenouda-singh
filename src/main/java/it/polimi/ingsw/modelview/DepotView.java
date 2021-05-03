package it.polimi.ingsw.modelview;

import it.polimi.ingsw.model.Origin;
import it.polimi.ingsw.model.Resource;

public class DepotView {
    final private Origin id;
    final private Resource resource;
    final private int capacity;

    public DepotView(Origin id, Resource resource, int capacity){
        this.id = id;
        this.resource = resource;
        this.capacity = capacity;
    }

    public Origin getId(){
        return id;
    }
    public Resource getResource() {
        return new Resource(resource.getQuantity(), resource.getResourceType());
    }

    public int getCapacity() {
        return capacity;
    }
}
