package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.observer.LambdaObservable;

import static it.polimi.ingsw.server.model.ResourceType.FAITH;
import static it.polimi.ingsw.server.model.ResourceType.JOLLY;

public class Depot {
	protected final int capacity;
	protected final Id id;
	protected Resource resource;

	public Depot(int capacity, Id id) {
		resource = null;
		this.id = id;
		this.capacity = capacity;
	}

	public Depot (int capacity, Id id, ResourceType resourceType){
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


		if(	other.getResourceType() == JOLLY || other.getResourceType() == FAITH 	){

			//throw new RuntimeException("Invalid resource type for depots");
			throw  new InvalidDepotException();

		} else if (	resource != null &&
				other.getResourceType() != this.resource.getResourceType() ){

			//throw new RuntimeException("Depot already contains a different type of resource");
			throw  new InvalidDepotException();
		} else if ( (resource != null ? resource.getQuantity() : 0) + other.getQuantity() < 0 ){

			//throw new RuntimeException("Depots may not contain negative resources");
			throw  new InvalidDepotException();

		} else if ((resource != null ? resource.getQuantity() : 0)  + other.getQuantity() > capacity) {

			//throw new RuntimeException("Resources would exceed depot capacity");
			throw  new InvalidDepotException();

		} else {
			if (resource != null) resource.add(other);
			else resource = other;

			if (resource!=null && resource.getQuantity() ==0){ resource = null;}
		}

	}

	public void subtractResource(Resource other) throws InvalidDepotException{


		if(	other.getResourceType() == JOLLY || other.getResourceType() == FAITH 	){

			//throw new RuntimeException("Invalid resource type for depots");
			throw  new InvalidDepotException();

		} else if (	resource != null &&
				other.getResourceType() != this.resource.getResourceType() ){

			//throw new RuntimeException("Depot already contains a different type of resource");
			throw  new InvalidDepotException();
		} else if ( (resource != null ? resource.getQuantity() : 0) - other.getQuantity() < 0 ){

			//throw new RuntimeException("Depots may not contain negative resources");
			throw  new InvalidDepotException();

		} else {
			if (resource != null) resource.sub(other);
			if (resource!=null && resource.getQuantity() ==0){ resource = null;}
		}

	}

	public int getCapacity() {
		return capacity;
	}

	public Id getId(){
		return id;
	}


}
