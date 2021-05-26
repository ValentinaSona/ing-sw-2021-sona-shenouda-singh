package it.polimi.ingsw.server.model;

public enum MarketMarble {

	RED, BLUE, PURPLE, YELLOW, WHITE, GREY;

	public ResourceType convertToResource(){
		return switch (this) {
			case RED -> ResourceType.FAITH;
			case BLUE -> ResourceType.SHIELD;
			case PURPLE -> ResourceType.SERVANT;
			case YELLOW -> ResourceType.COIN;
			case GREY -> ResourceType.STONE;
			case WHITE -> null;
		};
	}
}
