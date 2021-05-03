package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.VaticanReportException;

import java.util.ArrayList;

public class Player extends AbstractModel {

	private boolean isDisconnected;


	// private String nickname;

	private boolean isMyTurn;

	private boolean mainAction;

	private final FaithTrack faithTrack;

	private final LeaderCard[] leaderCards;

	private final Strongbox strongbox;

	private final Production boardProduction;

	private final DevelopmentCardSlot[] developmentCardSlots;

	private ArrayList<Depot> warehouse;





	// private LeaderCard[] tempLeaderCards


	private ArrayList<Resource> tempResources;

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
		warehouse = new ArrayList<>();
		warehouse.add(new Depot(1, Origin.DEPOT_1));
		warehouse.add(new Depot(2, Origin.DEPOT_2));
		warehouse.add(new Depot(3, Origin.DEPOT_3));

		developmentCardSlots = new DevelopmentCardSlot[]{new DevelopmentCardSlot(1),new DevelopmentCardSlot(2), new DevelopmentCardSlot(3)};
		// Instantiation of the faithTrack with the initial faith points.
		faithTrack = new FaithTrack();

		try {
			faithTrack.addFaithPoints(initialFaithPoints);
		} catch (VaticanReportException e) {
			e.printStackTrace();
		}


		this.leaderCards = leaderCards;

		this.strongbox = new Strongbox();
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
	 * Getter for the development card developmentCardSlots.
	 * @return all the development cards on the player's board.
	 */
	// TODO: Abstract superclass from Development card deck.
	public DevelopmentCardSlot[] getDevelopmentCardSlots() {
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
	public ArrayList<Depot> getWarehouse() {
		return warehouse;
	}

	public void addSpecialDepot(int capacity, ResourceType type, Origin origin){
		warehouse.add(new SpecialDepot(capacity, origin,  type));
	}

	public void throwError(String message){
		update(message, null, null);
	}

	/**
	 * This method is called when we succed in storing a resource taken from tempResource in a depot
	 * @param resource
	 */
	public void subFromTempResources(Resource resource){
		ArrayList<Resource> tempResources = getTempResources();

		for(Resource res : tempResources){
			if(res.getResourceType() == resource.getResourceType()){
				if(res.getQuantity() == resource.getQuantity()){
					tempResources.remove((Resource) res);
				}else{
					res.sub(resource);
				}
				break;
			}
		}
		update(TEMP_RESOURCES_UPDATE, null, tempResources);
	}

	public void addToTempResources(ArrayList<Resource> resources){
		this.tempResources = resources;
		update(TEMP_RESOURCES_UPDATE, null, tempResources);
	}

	public void dumpTempResources(){
		tempResources.clear();
		update(TEMP_RESOURCES_EMPTY, null, null);
	}

	public ArrayList<Resource> getTempResources() {
		return tempResources;
	}

}
