package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class Production implements Serializable {

	private final Resource[] productionCost;

	private final Resource[] productionOut;

	public Production(Resource[] productionCost, Resource[] productionOut) {

		this.productionCost = productionCost;
		this.productionOut = productionOut;

	}

	public Resource[] getProductionCost() {
		return  productionCost;
	}

	public Resource[] getProductionOut() {
		return productionOut;
	}


}
