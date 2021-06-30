package it.polimi.ingsw.server.model;

public enum ResourceType {

	SHIELD, SERVANT, STONE,	COIN, FAITH, JOLLY;


	public static ResourceType parseInput(String input){
		return Enum.valueOf(ResourceType.class, input.toUpperCase());
	}

	public MarketMarble convertToMarble(){
		return switch (this) {
			case FAITH -> MarketMarble.RED;
			case SHIELD -> MarketMarble.BLUE;
			case SERVANT -> MarketMarble.PURPLE;
			case COIN -> MarketMarble.YELLOW;
			case STONE -> MarketMarble.GREY;
			case JOLLY -> null;
		};
	}


}
