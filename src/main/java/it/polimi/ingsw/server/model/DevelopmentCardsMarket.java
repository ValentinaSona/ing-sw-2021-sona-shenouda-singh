package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.NotDecoratedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevelopmentCardsMarket implements  DevMarket {

	private final DevelopmentCardDeck[][] decks;

	public DevelopmentCardsMarket (DevelopmentCardDeck[][] decks) {
		this.decks = decks;
	}

	/**
	 * Remove the first card from the selected deck and returns it to the player
	 * @param row row of the selected deck
	 * @param col column of the selected deck
	 * @return the card on top of the selected deck
	 */
	public DevelopmentCard buyDevelopmentCard(int row, int col) {
		return decks[row][col].pickCard();
	}

	/**
	 * Get the first card from the selected deck without removing it and returns it to the player
	 * @param row row of the selected deck
	 * @param col column of the selected deck
	 * @return the card on top of the selected deck
	 */
	public DevelopmentCard getDevelopmentCard(Player player, int row, int col){
		return decks[row][col].firstCard();
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

	public DevMarketView getVisible() {

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

		return new DevMarketView(view);
	}

	/**
	 * Method called when a player activates a DiscountAbility
	 * The method builds a decorated DevelopmentCardsMarket and adds the new ability (with the respective player) to it
	 * @param discount the resource associated with the activated ability
	 * @param player the player who activated the ability
	 * @return the decorated market which will take into account the DiscountAbility
	 */
	public DevMarket addAbility(Resource discount, Player player) {
		HashMap<Player, List<Resource>> map = new HashMap<>();
		List<Resource> resourceList = new ArrayList<>();
		resourceList.add(discount);
		map.put(player, resourceList);

		return new DevelopmentCardsMarketAbility(this, map);
	}

	public HashMap<Player, List<Resource>> getMap() {
		throw new RuntimeException("No player has still activated a discount ability, invalid method");
	}

	/**
	 * Called by the solo action tokens of type discard.
	 * @param color The type of development cards to discard.
	 * @throws EndOfGameException when no development cards are left. The game is automatically lost.
	 */
	public void discard(DevelopmentType color) throws EndOfGameException {
		int type = 0;
		switch (color){
			case GREEN -> type = 0;
			case BLUE -> type = 1;
			case YELLOW -> type = 2;
			case PURPLE -> type = 3;
		}

		for (int i = 0; i < 3; i++){
			if  (decks[i][type].cardsLeft()!= 0){
				decks[i][type].pickCard();
				return;
			}
		}
		// If no cards are left of that type, the game has been lost.
		throw new EndOfGameException(true);

	}

}
