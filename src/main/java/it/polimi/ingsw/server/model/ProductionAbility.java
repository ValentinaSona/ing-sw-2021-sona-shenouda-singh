package it.polimi.ingsw.server.model;

public class ProductionAbility extends SpecialAbility {

	private final Resource cost;

	public ProductionAbility(Resource cost) {
		this.cost = cost;
	}

	public Resource getCost() {
		return cost;
	}

	@Override
	public String toString() {
		var p = new Production(new Resource[]{cost},new Resource[]{new Resource(1, ResourceType.JOLLY), new Resource(1, ResourceType.FAITH)});
		return p.toString();
	}
}
