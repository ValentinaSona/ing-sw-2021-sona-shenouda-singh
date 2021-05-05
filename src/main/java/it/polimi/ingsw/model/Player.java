package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.VaticanReportException;

import java.util.ArrayList;
import java.util.EmptyStackException;

public class Player extends AbstractModel {

	/**
	 * Set to true if the connection with the player fails and their turn is to be skipped.
	 */
	private boolean isDisconnected;

	/**
	 * The player's nickname, used to identify them in case of reconnection.
	 */
	private String nickname;

	/**
	 * True if it's the player's turn, false otherwise.
	 */
	private boolean isMyTurn;

	/**
	 * True if the player can still perform their action for this turn, false if it has already been used.
	 */
	private boolean mainAction;

	/**
	 * Player's personal Faith Track, handles faith points and pope favor tiles.
	 */
	private final FaithTrack faithTrack;

	private final LeaderCard[] leaderCards;

	/**
	 * Infinite depot where the production outputs are stored.
	 */
	private final Strongbox strongbox;

	/**
	 * Special production that turns 2 resources of any kind into one resource of any kind.
	 */
	private final Production boardProduction;

	private final DevelopmentCardSlot[] developmentCardSlots;

	private ArrayList<Depot> warehouse;





	private LeaderCard[] tempLeaderCards;


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

		// All the parameters are set to false until a method specifically activates them.
		isMyTurn = false;
		isDisconnected = false;
		mainAction = false;

		boardProduction = new Production(new Resource[]{new Resource(1,ResourceType.JOLLY), new Resource(1,ResourceType.JOLLY)}, new Resource[]{new Resource(1, ResourceType.JOLLY)});

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

		// Need to decide whether it's the first four or the permanent two
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
		mainAction = !mainAction;
	}


	/**
	 * Getter for the faithTrack.
	 * @return the player's faithTrack.
	 */
	public FaithTrack getFaithTrack() {
		return faithTrack;
	}

	/** 
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
	 * Getter for the development card developmentCardSlots.
	 * @return all the development cards on the player's board.
	 */
	public DevelopmentCardSlot[] getDevelopmentCardSlots() {
		return developmentCardSlots;
	}

	/**
	 * Getter for the visible development cards.
	 * @return array of the top three development cards, with null value if the stack is empty.
	 */
	public DevelopmentCard[] getVisibleCards(){
		DevelopmentCard[] cards = {null,null,null};

		try {
			cards[0] = developmentCardSlots[0].peek();
		} catch (EmptyStackException e) {
			cards[0] = null;
		}
		try {
			cards[1] = developmentCardSlots[1].peek();
		} catch (EmptyStackException e) {
			cards[1] = null;
		}
		try {
			cards[2] = developmentCardSlots[2].peek();
		} catch (EmptyStackException e) {
			cards[2] = null;
		}

		return cards;
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

	public void addSpecialDepot(int capacity, ResourceType type){
		Origin origin;
		if (warehouse.size()==3) {

			origin = Origin.S_DEPOT_1;
		} else  if (warehouse.size()==4) {
			origin = Origin.S_DEPOT_2;
		} else {
			throw new RuntimeException("Having more than two special depots should be impossible");
		}
		warehouse.add(new SpecialDepot(capacity, origin, type));
	}

	public void throwError(String message){
		update(message, null, null);
	}

	/**
	 * This method is called when we succeed in storing a resource taken from tempResource in a depot
	 * @param resource removes the stored resource from tempResources
	 */
	public void subFromTempResources(Resource resource){
		ArrayList<Resource> tempResources = getTempResources();

		for(Resource res : tempResources){
			if(res.getResourceType() == resource.getResourceType()){
				if(res.getQuantity() == resource.getQuantity()){
					tempResources.remove(res);
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

	public boolean canYouActivate(LeaderCard card){

		for (Requirement req : card.getRequirements()){

			int required;
			int possessed = 0;

			if (!req.isResource()) {
				required = req.getNumber();
				for (DevelopmentCardSlot slot : developmentCardSlots) {
					 possessed = (int) slot.getSlot().stream().filter(c -> c.getType() == req.getType() && (req.getLevel() == c.getLevel() || req.getLevel() == 0)).count();

				}

			} else {

				required = req.getResource().getQuantity();

				for (Depot depot: warehouse){
					if (depot.getResource().getResourceType()==req.getResource().getResourceType()){
						possessed += depot.getResource().getQuantity();
					}
				}

				possessed += strongbox.getAvailableResources(req.getResource().getResourceType()).getQuantity();

			}
			if (required > possessed) {return false;}
		}
		return true;
	}

}
