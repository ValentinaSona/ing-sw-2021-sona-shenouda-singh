package it.polimi.ingsw.server.model.observable;

import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;

//TODO replace  update() calls with notify() of the lambdaObservable class

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
                    //update(DEPOT_UPDATE, null, new DepotView(id, getResource(), capacity) );
                }else if(other.getQuantity() == resource.getQuantity()){
                    resource.setQuantity(0);
                }else{
                    //update(DEPOT_QUANTITY_ERROR, null, null);
                    throw  new InvalidDepotException();
                }
            }else{
                //update(DEPOT_TYPE_ERROR, null, null);
                throw new InvalidDepotException();
            }
        }else{
            //update(DEPOT_QUANTITY_ERROR, null , null);
            throw new InvalidDepotException();
        }
    }

}