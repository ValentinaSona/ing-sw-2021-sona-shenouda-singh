package it.polimi.ingsw.server.model;

import java.io.Serializable;
import java.util.Arrays;

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

	@Override
	public String toString() {
		return Arrays.toString(productionCost).substring(1, Arrays.toString(productionCost).length() - 1) +

				" -> " + Arrays.toString(productionOut).substring(1, Arrays.toString(productionOut).length() - 1);
	}
}
