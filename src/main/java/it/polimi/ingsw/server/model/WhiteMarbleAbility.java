package it.polimi.ingsw.server.model;

public class WhiteMarbleAbility extends SpecialAbility {

	private final MarketMarble marble;

	public WhiteMarbleAbility(MarketMarble marble) {
		this.marble = marble;
	}

	public MarketMarble getMarble() {
		return marble;
	}
}
