package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.InvalidDepotException;

import static it.polimi.ingsw.model.ResourceType.*;

public class Depot {

	private final int capacity;

	protected Resource resource;

	public Depot(int capacity) {
		resource = null;
		this.capacity = capacity;
	}

	public Depot (int capacity, ResourceType resourceType){
		resource = new Resource(0, resourceType);
		this.capacity = capacity;
	}

	/**
	 * Withdraw the resource from the depot
	 * @return resource of the depot, null if depot is empty and normal. Special depots return a resource with 0 quantity even if empty.
	 */
	public Resource withdrawResource() {
		Resource oldResource = resource;
		resource = null;
		return oldResource;
	}

	/**
	 * Getter for the depot's resources
	 * @return resource of the depot, null if depot is empty and normal. Special depots return a resource with 0 quantity even if empty.
	 */
	public Resource getResource(){
		return new Resource(resource.getQuantity(), resource.getResourceType());
	}

	public void addResource(Resource other) throws InvalidDepotException {

		if(	other.getResourceType() == JOLLY || other.getResourceType() == FAITH 	){

			throw new RuntimeException("Invalid resource type for depots");

		} else if (	resource != null && other.getResourceType() != resource.getResourceType() ){

			throw new InvalidDepotException("DIFFERENT_RESOURCES");

		} else if ( (resource != null ? resource.getQuantity() : 0) + other.getQuantity() < 0 ){

			throw new RuntimeException("Depots may not contain negative resources");


		} else if ((resource != null ? resource.getQuantity() : 0)  + other.getQuantity() > capacity) {

			throw new InvalidDepotException("CAPACITY");

		} else {
				if (resource != null){
					resource.add(other);
					if(resource.getQuantity() == 0){
						resource = null;
					}
				} else
					resource = other;
			}
	}
}
