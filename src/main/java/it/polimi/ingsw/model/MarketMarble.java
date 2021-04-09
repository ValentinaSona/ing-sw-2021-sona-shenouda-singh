package it.polimi.ingsw.model;

public enum MarketMarble {

	RED, BLUE, PURPLE, YELLOW, WHITE, GREY;

	public ResourceType convertToResource(){
		switch(this){
			case RED:
				return ResourceType.FAITH;
			case BLUE:
				return ResourceType.SHIELD;
			case PURPLE:
				return ResourceType.SERVANT;
			case YELLOW:
				return ResourceType.COIN;
			case GREY:
				return ResourceType.STONE;
			case WHITE:
				return null;
			default:
				throw new RuntimeException("Not a valid Marble");
		}
	}
}
