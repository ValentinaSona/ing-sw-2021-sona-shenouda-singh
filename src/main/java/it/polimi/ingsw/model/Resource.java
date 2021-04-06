package it.polimi.ingsw.model;

public class Resource {

	private int quantity;

	private final ResourceType resourceType;

	public int getQuantity() {
		return quantity;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public Resource(int quantity,ResourceType type) {
		resourceType = type;
		this.quantity = quantity;
	}

	public Resource(ResourceType type) {
		resourceType = type;
		this.quantity = 0;
	}

	// Adds another resource to this one.
	public void add(Resource other) {
		if (this.resourceType != other.getResourceType()) {
			throw new RuntimeException("Cannot sum different resources together!");
		} else {
			this.quantity += other.getQuantity();
		}
	}

}
