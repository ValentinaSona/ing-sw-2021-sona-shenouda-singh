package it.polimi.ingsw.server.model;

public class ExtraDepotAbility extends SpecialAbility {

	private final int capacity;
	private final ResourceType type;

	public ExtraDepotAbility(int capacity, ResourceType type) {
		this.capacity = capacity;
		this.type = type;
	}

	public int getCapacity() {
		return capacity;
	}

	public ResourceType getType() {
		return type;
	}
}
