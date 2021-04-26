package it.polimi.ingsw.model;

public class LeaderCard extends SpecialAbility {

	private Boolean isActive;


	private final int victoryPoints;

	// TODO: error in reading the specifics, this is not a resource but refers to devel card. Ask pepe about representation.
	// private Resource resource;


	// TODO: check with market.
	private final SpecialAbility specialAbility;

	public LeaderCard (int victoryPoints, SpecialAbility specialAbility){
		this.isActive = false;
		// TODO: requirements when specified also getters.
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
