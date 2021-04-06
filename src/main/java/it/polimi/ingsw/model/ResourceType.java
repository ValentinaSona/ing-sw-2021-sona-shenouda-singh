package it.polimi.ingsw.model;

public enum ResourceType {

	SHIELD, SERVANT, STONE,	COIN, FAITH, JOLLY;


	public static ResourceType parseInput(String input){
		return Enum.valueOf(ResourceType.class, input.toUpperCase());
	}


}
