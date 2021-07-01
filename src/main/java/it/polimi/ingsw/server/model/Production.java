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

    public Production(Production production) {
		var cost = new Resource[production.getProductionCost().length];
		var out = new Resource[production.getProductionOut().length];

		for (int i = 0; i < production.getProductionCost().length; i++) {
			cost[i] = new Resource(production.getProductionCost()[i]);
		}
		for (int i = 0; i < production.getProductionOut().length; i++) {
			out[i] = new Resource(production.getProductionOut()[i]);
		}

		productionCost = cost;
		productionOut = out;
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
