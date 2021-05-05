package it.polimi.ingsw.model;

public class WhiteMarbleAbility extends SpecialAbility {

	private MarketMarble marble;

	public WhiteMarbleAbility(MarketMarble marble) {
		this.marble = marble;
	}

	public MarketMarble getMarble() {
		return marble;
	}
}
