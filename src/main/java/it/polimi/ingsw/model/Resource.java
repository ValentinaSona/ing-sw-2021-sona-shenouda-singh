package it.polimi.ingsw.model;

import java.util.Objects;

public class Resource {


	private int quantity;

	private final ResourceType resourceType;

	public Resource(int quantity,ResourceType type) {
		resourceType = type;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public ResourceType getResourceType() {
		return resourceType;
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


	// Adds the argument to the resource
	public void add(Resource other) {
		if (this.resourceType != other.getResourceType()) {
			throw new RuntimeException("Cannot sum different resources together!");
		} else {
			this.setQuantity(this.quantity + other.getQuantity());
		}
    }

    public void sub(Resource other){
		if(this.getResourceType() != other.getResourceType()){
			throw new RuntimeException("Can not subtract different resources together!");
		}else{
			int newValue = this.getQuantity()- other.getQuantity();
			if(newValue >= 0){
				this.setQuantity(newValue);
			}else{
				throw new RuntimeException("Can not have a quantity under 0");
			}
		}
	}

}
