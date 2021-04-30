package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardsMarket {

	private DevelopmentCardDeck[][] decks;

	public DevelopmentCardsMarket (DevelopmentCardDeck[][] decks) {
		this.decks = decks;
	}

	/**
	 * Remove the first card from the selected deck and returns it to the player
	 * @param row row of the selected deck
	 * @param col column of the selected deck
	 * @return the card on top of the selected deck
	 */
	public DevelopmentCard buyDevelopmentCards(int row, int col) {
		return decks[row][col].pickCard();
	}

	/**
	 * Shuffles all the decks
	 */
	public void shuffle() {

		for(int i = 0; i < 3; i++) {
			for(int j= 0; j < 4; j++) {
				decks[i][j].shuffle();
			}
		}

	}

	/**
	 * @return a copy of the array containing all decks in the market
	 */
	public DevelopmentCardDeck[][] getDecks() {
		DevelopmentCardDeck[][] deckView = new DevelopmentCardDeck[3][4];
		System.arraycopy(decks, 0, deckView, 0, 3);

		return deckView;
	}

	/**
	 * @return a 2d-array containing all the cards on top of the decks
	 * if the decks[i][j] is empty view[i][j] = null
	 */
	public DevelopmentCard[][] getVisible() {
		DevelopmentCard[][] view = new DevelopmentCard[3][4];

		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 4; j++) {

				try{
					view[i][j] = decks[i][j].firstCard();
				} catch (RuntimeException e) {
					view[i][j] = null;
				}
			}
		}

		return view;
	}
}
