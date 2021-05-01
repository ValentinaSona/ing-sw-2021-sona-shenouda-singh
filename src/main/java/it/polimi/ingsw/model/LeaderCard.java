package it.polimi.ingsw.model;

public class LeaderCard extends SpecialAbility {

	private Boolean isActive;


	private final int victoryPoints;

	// private requirements

	private final SpecialAbility specialAbility;

	public LeaderCard (int victoryPoints, SpecialAbility specialAbility){
		this.isActive = false;
		// requirements when specified also getters.
		this.victoryPoints = victoryPoints;
		this.specialAbility = specialAbility;
	}
	public Boolean isActive() {
		return isActive;
	}

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
