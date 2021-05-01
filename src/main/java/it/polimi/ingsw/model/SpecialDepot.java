package it.polimi.ingsw.model;

public class SpecialDepot extends Depot{

    public SpecialDepot(int capacity, ResourceType resourceType){
        super(capacity, resourceType);
    }

    @Override
    public  Resource withdrawResource(){
        ResourceType type = getResource().getResourceType();
        Resource oldResource = super.withdrawResource();
        resource = new Resource(0, type);

        return oldResource;
    }

}
