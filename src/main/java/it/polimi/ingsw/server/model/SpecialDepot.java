package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.InvalidDepotException;

public class SpecialDepot extends Depot {

    public SpecialDepot(int capacity, Id id, ResourceType resourceType){
        super(capacity, id, resourceType);
    }

    @Override
    public void subtractResource(Resource other) throws InvalidDepotException {

        if(resource != null){

            if(other.getResourceType() == resource.getResourceType()){

                if(resource.getQuantity() > other.getQuantity()){

                    resource.sub(other);

                }else if(other.getQuantity() == resource.getQuantity()){
                    resource.setQuantity(0);
                }else{
                    throw  new InvalidDepotException();
                }
            }else{
                throw new InvalidDepotException();
            }
        }else{
            throw new InvalidDepotException();
        }
    }

}
