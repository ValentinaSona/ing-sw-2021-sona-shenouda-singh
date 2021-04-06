package it.polimi.ingsw.model;

import java.util.Objects;

public class Resource {

	private final int quantity;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Resource resource = (Resource) o;
		return quantity == resource.quantity && resourceType == resource.resourceType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(quantity, resourceType);
	}

	// Adds another resource to this one.
	public Resource add(Resource other) {
		if (this.resourceType != other.getResourceType()) {
			throw new RuntimeException("Cannot sum different resources together!");
		} else {
			return new Resource(this.quantity + other.getQuantity(), this.resourceType);
		}
	}

}
