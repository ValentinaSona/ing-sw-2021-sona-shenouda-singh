package it.polimi.ingsw.model;

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

	public void setResource(Resource newResource){

		if (newResource.getQuantity() > capacity) {

			throw new RuntimeException("Resources exceed depot capacity");

		} else if ( newResource.getQuantity() < 0 ){

			throw new RuntimeException("Depots may not contain negative resources");

		} else if (	newResource.getResourceType() == ResourceType.JOLLY ||
					newResource.getResourceType() == ResourceType.FAITH 	){

			throw new RuntimeException("Invalid resource type for depots");

		} else if (	this.resource != null &&
					newResource.getResourceType() != this.resource.getResourceType() ){

			throw new RuntimeException("Depot already contains a different type of resource");

		} else {
			resource = newResource;
		}

	}

	public void setNullIfEmpty(){
		if (resource.getQuantity() == 0) {
			resource = null;
		}
	}

}
