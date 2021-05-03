package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.InvalidDepotException;
import it.polimi.ingsw.modelview.DepotView;

import static it.polimi.ingsw.model.ResourceType.*;

public class Depot extends AbstractModel {
	protected final int capacity;
	protected final Origin id;
	protected Resource resource;


	public Depot(int capacity, Origin id) {
		resource = null;
		this.id = id;
		this.capacity = capacity;
	}

	public Depot (int capacity, Origin id, ResourceType resourceType){
		this(capacity,id);
		resource = new Resource(0, resourceType);
	}

	/**
	 * Getter for the depot's resources it returns a copy of the one stored
	 * @return resource of the depot, null if depot is empty and normal. Special depots return a resource with 0 quantity even if empty.
	 */
	public Resource getResource(){
		if(resource != null)
			return new Resource(resource.getQuantity(), resource.getResourceType());
		else
			return null;
	}

	public void addResource(Resource other) throws InvalidDepotException {
		if(resource == null){
			if(other.getResourceType() != JOLLY && other.getResourceType() != FAITH && other.getQuantity()<= capacity){
				resource.add(other);
				update(DEPOT_UPDATE, null, new DepotView(id, getResource(), capacity) );
			}else{
				update(DEPOT_QUANTITY_ERROR, null, null);
				throw  new InvalidDepotException();
			}
		}else{
			if(resource.getResourceType() == other.getResourceType()){
				int newValue = resource.getQuantity()+other.getQuantity();
				if(newValue <= capacity){
					resource.add(other);
					update(DEPOT_UPDATE, null, new DepotView(id, getResource(), capacity) );
				}else {
					update(DEPOT_QUANTITY_ERROR, null, null);
					throw new InvalidDepotException();
				}
			}else{
				update(DEPOT_TYPE_ERROR, null, null);
				throw  new InvalidDepotException();
			}
		}
	}

	public void subtractResource(Resource other) throws InvalidDepotException{

		if(resource != null){
			if(other.getResourceType() == resource.getResourceType()){
				if(resource.getQuantity() > other.getQuantity()){
					resource.sub(other);
					update(DEPOT_UPDATE, null, new DepotView(id, getResource(), capacity) );
				}else if(other.getQuantity() == resource.getQuantity()){
					resource = null;
				}else{
					update(DEPOT_QUANTITY_ERROR, null, null);
					throw  new InvalidDepotException();
				}
			}else{
				update(DEPOT_TYPE_ERROR, null, null);
				throw new InvalidDepotException();
			}
		}else{
			update(DEPOT_QUANTITY_ERROR, null, null);
			throw new InvalidDepotException();
		}
	}

	public int getCapacity() {
		return capacity;
	}
}
