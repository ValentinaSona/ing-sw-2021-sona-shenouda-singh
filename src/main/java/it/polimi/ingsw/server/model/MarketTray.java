package it.polimi.ingsw.server.model;

import java.util.*;

public class MarketTray implements Market {

	private MarketMarble[][] marketMarbles;

	private MarketMarble extra;

	public MarketTray(MarketMarble[][] marketMarbles, MarketMarble extra) {

		this.marketMarbles =  marketMarbles;
		this.extra = extra;

	}

	/**
	 * This method inserts the extra marble into the selected row-column, pushing the other marbles and creating a new extra marble
	 * @param rowCol is the number corresponding to the row or column where to insert the marble: 0-2 -> rows, 3-6 -> columns
	 */
	private void insertIntoMatrix (int rowCol) {

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

	/**
	 * This method copies the selected line in the market tray and modifies it by calling insertIntoMatrix, then returns the line to the caller
	 * @param player the player who's doing the action
	 * @param rowCol is the number corresponding to the row or column where to insert the marble: 0-2 -> rows, 3-6 -> columns
	 * @return the line of marbles selected by the player
	 */
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

	/**
	 * Utility function for copying a column of a matrix
	 * @param matrix the source matrix from where to copy
	 * @param col the index of the column to copy
	 * @return an array with the elements of the column
	 */
	private MarketMarble[] colCopy (MarketMarble[][] matrix, int col) {

		MarketMarble[] array = new MarketMarble[matrix.length];

		for (int i = 0; i < matrix.length; i++) {
			array[i] = matrix[i][col];
		}

		return array;
	}

	/**
	 * Creates a decorated version of the actual market, MarketTrayAbility, to enable white marble abilities
	 * @param marble the white marble ability activated by the player
	 * @param player the player who activates the ability
	 * @return the decorated market containing this
	 */
	public Market addAbility (MarketMarble marble, Player player) {

		// Creates a map that will be used to keep track of player's abilities in the market
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

	public MarketMarble getExtra() {
		return extra;
	}

	public MarketMarble[] getChosen(MarketMarble[] choice){
		throw new RuntimeException("No player has still activated a white marble ability, invalid method");
	}

	public HashMap <Player, List<MarketMarble>> getAbilityMap() {
		throw new RuntimeException("No player has still activated a white marble ability, invalid method");
	}

}
