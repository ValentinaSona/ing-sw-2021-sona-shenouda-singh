package it.polimi.ingsw.server.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class LeaderCard extends SpecialAbility implements Serializable {

	private final int id;

	private Boolean isActive;

	private final Requirement[] requirements;

	private final int victoryPoints;

	private final SpecialAbility specialAbility;

	public LeaderCard(int id, Requirement[] requirements, int victoryPoints, SpecialAbility specialAbility){
		this.id = id;
		this.requirements = requirements;
		this.isActive = false;
		this.victoryPoints = victoryPoints;
		this.specialAbility = specialAbility;
	}

	public LeaderCard(int id, Requirement[] requirements, int victoryPoints, SpecialAbility specialAbility, String abilityType){
		this.id = id;
		this.requirements = requirements;
		this.isActive = false;
		this.victoryPoints = victoryPoints;
		this.specialAbility = specialAbility;
		specialAbility.setAbilityType(abilityType);
	}
	public Boolean isActive() {
		return isActive;
	}

	public Requirement[] getRequirements() { return requirements; }

	public void setActive(Boolean active) {
		isActive = active;
	}

	public int getVictoryPoints() {
		return victoryPoints;
	}

	public SpecialAbility getSpecialAbility() {
		return specialAbility;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {

		return "" +
				"\tRequirements: " + Arrays.toString(requirements).substring(1, Arrays.toString(requirements).length() - 1)+ "\n" +

				"\tSpecial ability: " + specialAbility.toString() + "\n"+
				"\tVictory points: " + victoryPoints + "\n";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LeaderCard that = (LeaderCard) o;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
