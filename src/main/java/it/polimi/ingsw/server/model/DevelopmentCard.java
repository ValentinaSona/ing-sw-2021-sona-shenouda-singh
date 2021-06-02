package it.polimi.ingsw.server.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class DevelopmentCard implements Serializable {

	final private int id;

	final private Resource[] cost;

	final private DevelopmentType type;

	final private int level;

	final private int victoryPoints;

	final private Production production;

	public DevelopmentCard(int id, Resource[] cost, DevelopmentType type, int level, int victoryPoints, Production production) {
		this.id = id;
		this.cost = cost;
		this.type = type;
		this.level = level;
		this.victoryPoints = victoryPoints;
		this.production = production;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DevelopmentCard that = (DevelopmentCard) o;
		return id == that.id && level == that.level && victoryPoints == that.victoryPoints && Arrays.equals(cost, that.cost) && type == that.type && production.equals(that.production);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(id, type, level, victoryPoints, production);
		result = 31 * result + Arrays.hashCode(cost);
		return result;
	}

	/**
	 * Create a card with a modified cost
	 * @param cost the now cost
	 * @param card the old card
	 */
	public DevelopmentCard(Resource[] cost, DevelopmentCard card) {
		this.id = card.getId();
		this.cost = cost;
		this.type = card.getType();
		this.level = card.getLevel();
		this.victoryPoints = card.getVictoryPoints();
		this.production = card.getProduction();
	}

	public int getId() {
		return id;
	}

	public Resource[] getCost() {
		return cost;
	}

	public DevelopmentType getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public Production getProduction() {
		return production;
	}

}
