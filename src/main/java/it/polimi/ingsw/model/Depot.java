package it.polimi.ingsw.model;

import static it.polimi.ingsw.model.ResourceType.*;

public class Depot {

	private final int capacity;

	private Resource resource;

	public Depot(int capacity) {
		resource = null;
		this.capacity = capacity;
	}

	public Resource getResource() {
		return resource;
	}

	public void addResource(Resource other){

			if(	other.getResourceType() == JOLLY || other.getResourceType() == FAITH 	){

				throw new RuntimeException("Invalid resource type for depots");

			} else if (	resource != null &&
					other.getResourceType() != this.resource.getResourceType() ){

				throw new RuntimeException("Depot already contains a different type of resource");

			} else if ( resource.getQuantity() + other.getQuantity() < 0 ){

				throw new RuntimeException("Depots may not contain negative resources");


			} else if (resource.getQuantity() + other.getQuantity() > capacity) {

				throw new RuntimeException("Resources would exceed depot capacity");


		} else {
			resource.add(other);
		}

	}

	public void setNullIfEmpty(){
		if (resource.getQuantity() == 0) {
			resource = null;
		}
	}

}
