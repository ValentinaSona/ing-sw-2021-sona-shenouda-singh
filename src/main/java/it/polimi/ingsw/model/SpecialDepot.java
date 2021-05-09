package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.InvalidDepotException;
import it.polimi.ingsw.modelview.DepotView;

public class SpecialDepot extends Depot{

    public SpecialDepot(int capacity, Id id, ResourceType resourceType){
        super(capacity, id, resourceType);
    }

    @Override
    public void subtractResource(Resource other) throws InvalidDepotException {

        if(resource != null){
            if(other.getResourceType() == resource.getResourceType()){
                if(resource.getQuantity() > other.getQuantity()){
                    resource.sub(other);
                    update(DEPOT_UPDATE, null, new DepotView(id, getResource(), capacity) );
                }else if(other.getQuantity() == resource.getQuantity()){
                    resource.setQuantity(0);
                }else{
                    update(DEPOT_QUANTITY_ERROR, null, null);
                    throw  new InvalidDepotException();
                }
            }else{
                update(DEPOT_TYPE_ERROR, null, null);
                throw new InvalidDepotException();
            }
        }else{
            update(DEPOT_QUANTITY_ERROR, null , null);
            throw new InvalidDepotException();
        }
    }

}
