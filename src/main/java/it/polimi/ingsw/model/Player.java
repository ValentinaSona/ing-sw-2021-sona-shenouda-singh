package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.VaticanReportException;

public class Player {

	private boolean isDisconnected;


	// private String nickname;

	private boolean isMyTurn;

	private boolean mainAction;

	private final FaithTrack faithTrack;

	private final LeaderCard[] leaderCards;

	private final Strongbox strongbox;

	private final Production boardProduction;



	private Depot[] warehouse;

	private DevelopmentCardDeck[] developmentCardSlots;






	// private LeaderCard[] tempLeaderCards


	private Resource[] tempResources;

	public Resource[] getTempResources() {
		return tempResources;
	}

	public void setTempResources(Resource[] tempResources) {
		this.tempResources = tempResources;
	}

	/** TODO: when is the player interaction for the initial resources made? May need to change type to resource
	 ** TODO: likewise, when is the leaderCard interaction made? Doesn't the constructor need to have 2 or 4 as input?
	 ** TODO: does it really need the views? Or are they added manually? Should be an array anyway no?
	 * Constructor
	 *
	 * @param initialResources initial resources distributed to the player.
	 * @param initialFaithPoints initial faith points attributed to the player.
	 */

	public Player(int initialResources, int initialFaithPoints, LeaderCard[] leaderCards /*, view View*/){

		// all the parameters are set to false until a method specifically activates them.
		isMyTurn = false;
		isDisconnected = false;
		mainAction = false;

		boardProduction = new Production(new Resource[]{new Resource(2,ResourceType.JOLLY)}, new Resource[]{new Resource(1, ResourceType.JOLLY)});
		warehouse = new Depot[]{new Depot(1), new Depot(2), new Depot(3)};

		// Instantiation of the faithTrack with the initial faith points.
		faithTrack = new FaithTrack();

		try {
			faithTrack.addFaithPoints(initialFaithPoints);
		} catch (VaticanReportException e) {
			e.printStackTrace();
		}


		this.leaderCards = leaderCards;

		this.strongbox = new Strongbox();

		this.developmentCardSlots = new DevelopmentCardDeck[] {null, null, null};
		}

	/**
	 * Getter for boolean isMyTurn.
	 * @return true if it is the player's turn, false otherwise.
	 */
	public boolean getTurn() {
		return isMyTurn;
	}

	/**
	 * Toggles player's turn from false to true and from true to false.
	 */
	public void toggleTurn() {
		isMyTurn = !isMyTurn;
	}



	/**
	 * Getter for boolean mainAction
	 * @return true if the player can still use their action for the turn, false otherwise.
	 */
	public boolean getMainAction() {
		return mainAction;
	}

	/**
	 * Expends a player's action, invoked by the respective controllers.
	 */
	public void toggleMainAction() {
		this.mainAction = !this.mainAction;
	}


	/**
	 * Getter for the faithTrack.
	 * @return the player's faithTrack.
	 */
	public FaithTrack getFaithTrack() {
		return faithTrack;
	}

	/** TODO: ask if return array or single element.
	 * Getter for the leaderCards.
	 * @return the player's leaderCards.
	 */
	public LeaderCard[] getLeaderCards() {
		return leaderCards;
	}

	/**
	 * Getter for the strongbox.
	 * @return the player's strongbox.
	 */
	public Strongbox getStrongbox() {
		return strongbox;
	}

	/**
	 ** TODO: getVisibleCards method?
	 * Getter for the development card slots.
	 * @return all the development cards on the player's board.
	 */
	// TODO: Abstract superclass from Development card deck.
	public DevelopmentCardDeck[] getDevelopmentCardSlots() {
		return developmentCardSlots;
	}


	/**
	 * Getter for player's board production.
	 * @return the board production.
	 */
	public Production getBoardProduction() {
		return boardProduction;
	}

	/**
	 * Getter for the warehouse.
	 * @return the player's warehouse.
	 */
	public Depot[] getWarehouse() {
		return warehouse;
	}


}
