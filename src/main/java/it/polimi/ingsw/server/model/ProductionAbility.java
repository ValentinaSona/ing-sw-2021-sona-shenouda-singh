package it.polimi.ingsw.server.model;

public class ProductionAbility extends SpecialAbility {

	private final Resource cost;

	public ProductionAbility(Resource cost) {
		this.cost = cost;
	}

	public Resource getCost() {
		return cost;
	}
}
