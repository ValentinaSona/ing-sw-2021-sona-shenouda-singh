package it.polimi.ingsw.server.model;

import java.io.Serializable;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public enum MarketMarble implements Serializable {

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


	public String toColorString() {
		String marble = "";
		switch (this){
			case RED -> {marble = ANSI_RED + CIRCLE + ANSI_RESET + " ";}
			case WHITE -> {marble =  WHITE_CIRCLE +  " ";}
			case BLUE -> {marble = ANSI_BLUE + CIRCLE + ANSI_RESET + " ";}
			case YELLOW -> {marble = ANSI_YELLOW + CIRCLE + ANSI_RESET + " ";}
			case GREY -> {marble = ANSI_WHITE + CIRCLE + ANSI_RESET + " ";}
			case PURPLE -> {marble = ANSI_PURPLE + CIRCLE + ANSI_RESET + " ";}

		}
		return marble;
	}
}
