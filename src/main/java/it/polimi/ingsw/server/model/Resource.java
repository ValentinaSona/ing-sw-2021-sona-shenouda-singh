package it.polimi.ingsw.server.model;

import java.io.Serializable;
import java.util.Objects;

public class Resource implements Serializable {


	private int quantity;

	private final ResourceType resourceType;

	public Resource(int quantity,ResourceType type) {
		resourceType = type;
		this.quantity = quantity;
	}

	public Resource(Resource resource) {
		quantity = resource.getQuantity();
		resourceType = resource.getResourceType();
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

	@Override
	public String toString() {
		switch (resourceType) {
			case SHIELD -> {
				return "\033[34m" + quantity + " " + resourceType + "\033[0m";
			}
			case SERVANT -> {
				return "\033[35m" + quantity + " " + resourceType + "\033[0m";
			}
			case FAITH -> {
				return "\033[31m" + quantity + " " + resourceType + "\033[0m";
			}
			case STONE -> {
				return "\033[37m" + quantity + " " + resourceType + "\033[0m";
			}
			case COIN -> {
				return "\033[33m" + quantity + " " + resourceType + "\033[0m";
			}
			case JOLLY -> {
				return quantity + " " + resourceType;
			}
			default -> {
				return "";
			}
		}
	}

	// Adds the argument to the resource
	public void add(Resource other) {
		if (this.resourceType != other.getResourceType()) {
			throw new RuntimeException("Cannot sum different resources together!");
		} else if (other != null){
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
