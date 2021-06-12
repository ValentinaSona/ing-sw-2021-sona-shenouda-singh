package it.polimi.ingsw.server.model;

import java.util.*;

public class DevelopmentCardDeck {

	private List<DevelopmentCard> deck;

	private final int level;
	private final DevelopmentType color;

	public DevelopmentCardDeck( List<DevelopmentCard> deck, int level, DevelopmentType color ) {
		this.deck = deck;
		this.level = level;
		this.color = color;

		Collections.shuffle(deck);
	}

	/**
	 * Shuffles the deck
	 */
	public void shuffle() {
		Collections.shuffle(deck);
	}

	/**
	 * This method is for getting the first card without removing it from the deck
	 * @return the first card in the deck
	 */
	public DevelopmentCard firstCard() {

		if(deck.isEmpty()) throw new RuntimeException("There are no cards left in this deck");
		return deck.get(0);

	}

	/**
	 * Removes the first card and returns it to teh caller
	 * @return the first card in the deck
	 */
	public DevelopmentCard pickCard() {

		if(deck.isEmpty()) throw new RuntimeException("There are no cards left in this deck");
		DevelopmentCard card = deck.get(0);
		deck.remove(0);
		return card;
		
	}

	/**
	 * @return the number of cards left in the deck
	 */
	public int cardsLeft() {
		return deck.size();
	}

	public int getLevel() {
		return level;
	}

	public DevelopmentType getColor() {
		return color;
	}

	public boolean isEmpty(){return deck.isEmpty();}

}
