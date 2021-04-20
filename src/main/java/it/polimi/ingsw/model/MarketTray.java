package it.polimi.ingsw.model;

import java.util.*;

public class MarketTray implements Market {

	private MarketMarble[][] marketMarbles;

	private MarketMarble extra;

	public MarketTray(MarketMarble[][] marketMarbles, MarketMarble extra) {

		this.marketMarbles =  marketMarbles;
		this.extra = extra;

	}

	// rowCol = 0 - 2 -> rows
	// rowCol = 3 - 6 -> columns
	public void insertIntoMatrix (int rowCol) {

		MarketMarble temp;

		if (rowCol < 3) {

			temp = marketMarbles[rowCol][0];

			System.arraycopy(marketMarbles[rowCol], 1, marketMarbles[rowCol], 0, 3);

			marketMarbles[rowCol][3] = extra;

		}

		else {

			temp = marketMarbles[0][rowCol - 3];

			for(int i = 1; i < 3; i++) marketMarbles[i-1][rowCol-3] = marketMarbles[i][rowCol-3];

			marketMarbles[2][rowCol-3] = extra;

		}
		extra = temp;
	}

	// rowCol = 0 - 2 -> rows
	// rowCol = 3 - 6 -> columns
	public MarketMarble[] getResources (Player player, int rowCol) {

		MarketMarble[] resources;

		if(rowCol < 3) {
			resources = new MarketMarble[marketMarbles[rowCol].length];
			System.arraycopy(marketMarbles[rowCol], 0, resources, 0, marketMarbles[rowCol].length);
		}

		else resources = colCopy(marketMarbles, rowCol-3);

		insertIntoMatrix(rowCol);

		return resources;

	}

	// Creates an array of a column of a matrix
	public MarketMarble[] colCopy (MarketMarble[][] matrix, int col) {

		MarketMarble[] array = new MarketMarble[matrix.length];

		for (int i = 0; i < matrix.length; i++) {
			array[i] = matrix[i][col];
		}

		return array;
	}

	// This method returns a decorated marketTray that supports white marble abilities
	public Market addAbility (MarketMarble marble, Player player) {

		HashMap<Player, List<MarketMarble>> abilityMap = new HashMap<>();
		List<MarketMarble> playerAbilities = new ArrayList<>();

		playerAbilities.add(marble);
		abilityMap.put(player, playerAbilities);

		return new MarketTrayAbility(this, abilityMap);
	}

	public MarketMarble[][] getTray() {
		MarketMarble[][] tray = new MarketMarble[3][4];
		System.arraycopy(marketMarbles, 0, tray, 0, marketMarbles.length);
		return tray;
	}

	public MarketMarble getExtra() {return extra;}

}
