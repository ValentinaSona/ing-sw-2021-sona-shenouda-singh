package it.polimi.ingsw.model;

public class LeaderCard extends SpecialAbility {

	private Boolean isActive;

	private final Requirement[] requirements;

	private final int victoryPoints;

	private final SpecialAbility specialAbility;

	public LeaderCard(Requirement[] requirements, int victoryPoints, SpecialAbility specialAbility){
		this.requirements = requirements;
		this.isActive = false;
		this.victoryPoints = victoryPoints;
		this.specialAbility = specialAbility;
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
}
