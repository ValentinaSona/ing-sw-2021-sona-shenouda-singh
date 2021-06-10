package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.*;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObservable;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.stream.Collectors;

//TODO does player really need to be observable??
public class Player extends LambdaObservable<Transmittable> {

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

	private List<LeaderCard> leaderCards = new ArrayList<>();

	/**
	 * Infinite depot where the production outputs are stored.
	 */
	private final Strongbox strongbox;

	/**
	 * Special production that turns 2 resources of any kind into one resource of any kind.
	 */
	private List<Slot> slots =  new ArrayList<>();

	private List<Depot> warehouse = new ArrayList<>();

	private List<Resource> tempResources;

	/** TODO: when is the player interaction for the initial resources made? May need to change type to resource
	 ** TODO: likewise, when is the leaderCard interaction made? Doesn't the constructor need to have 2 or 4 as input?
	 ** TODO: does it really need the views? Or are they added manually? Should be an array anyway no?
	 * Constructor
	 */

	public Player(String nickname){

		// All the parameters are set to false until a method specifically activates them.
		this.nickname = nickname;
		isMyTurn = false;
		isDisconnected = false;
		mainAction = false;
		strongbox = new Strongbox();

		warehouse.add(new Depot(1, Id.DEPOT_1));
		warehouse.add(new Depot(2, Id.DEPOT_2));
		warehouse.add(new Depot(3, Id.DEPOT_3));

		slots.add(new BoardProduction(Id.BOARD_PRODUCTION));
		slots.add(new DevelopmentCardSlot(Id.SLOT_1));
		slots.add(new DevelopmentCardSlot(Id.SLOT_2));
		slots.add(new DevelopmentCardSlot(Id.SLOT_3));


		faithTrack = new FaithTrack();
	}

	public Player(PlayerView playerView, List<Slot> slots) {

		nickname = playerView.getNickname();
		isMyTurn = false;
		isDisconnected = false;
		mainAction = false;

		strongbox = new Strongbox(playerView.getStrongboxView());
		warehouse = playerView.getWarehouse().stream().map(Depot::new).collect(Collectors.toList());
		this.slots = slots;
		faithTrack = new FaithTrack(playerView.getFaithTrackView());

		leaderCards = playerView.getLeaderCards();
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
	public List<LeaderCard> getLeaderCards() {
		return leaderCards;
	}

	public void setLeaderCards(LeaderCard[] leaderCards){
		this.leaderCards.add(leaderCards[0]);
		this.leaderCards.add(leaderCards[1]);
	}
	/**
	 * Getter for the strongbox.
	 * @return the player's strongbox.
	 */
	public Strongbox getStrongbox() {
		return strongbox;
	}

	/**
	 * Returns the total resources one player possesses for one type.
	 * @param type the ResourceType to get the total of.
	 * @return Resource that represents the total the player possesses.
	 */
	public Resource getTotalResources(ResourceType type){
		Resource total = new Resource(0,type);
		total.add(strongbox.getAvailableResources(type));

		for(Depot depot : warehouse){
			if (depot.getResource().getResourceType() == type){
				total.add(depot.getResource());
			}
		}

		return total;
	}

	/**
	 * Getter for the sum of all player resources.
	 * @return Resource array with the total of all resources the player possesses.
	 */
	public Resource[] getTotalResources(){
		return new Resource[]{getTotalResources(ResourceType.SHIELD), getTotalResources(ResourceType.STONE), getTotalResources(ResourceType.SERVANT), getTotalResources(ResourceType.COIN)};
	}

	/**
	 * Checks whether the player has enough resources to pay.
	 * @param cost Resource array of the required resources.
	 * @return true if player has enough, false otherwise.
	 */
	public boolean canPay(Resource[] cost){
		for (Resource resource : cost){
			if (resource.getQuantity() > getTotalResources(resource.getResourceType()).getQuantity()){return false;}
		}
		return true;
	}


	/**
	 * Getter for the development card developmentCardSlots.
	 * @return all the development cards on the player's board.
	 */
	public DevelopmentCardSlot[] getDevelopmentCardSlots() {

		DevelopmentCardSlot slot1 = (DevelopmentCardSlot) slots.get(Id.SLOT_1.getValue());
		DevelopmentCardSlot slot2 = (DevelopmentCardSlot) slots.get(Id.SLOT_2.getValue());
		DevelopmentCardSlot slot3 = (DevelopmentCardSlot) slots.get(Id.SLOT_3.getValue());

		return new DevelopmentCardSlot[]{slot1, slot2, slot3};


	}

	/**
	 * Getter for the all types of production slot
	 * @return all the development cards on the player's board.
	 */
	public List<Slot> getSlots() {
		return slots;
	}

	/**
	 * Getter for the visible development cards.
	 * @return array of the top three development cards, with null value if the stack is empty.
	 */
	public DevelopmentCard[] getVisibleCards(){
		DevelopmentCard[] cards = {null,null,null};
		DevelopmentCardSlot slot1 = (DevelopmentCardSlot) slots.get(Id.SLOT_1.getValue());
		DevelopmentCardSlot slot2 = (DevelopmentCardSlot) slots.get(Id.SLOT_2.getValue());
		DevelopmentCardSlot slot3 = (DevelopmentCardSlot) slots.get(Id.SLOT_3.getValue());

		try {
			cards[0] = slot1.peek();
		} catch (EmptyStackException e) {
			cards[0] = null;
		}
		try {
			cards[1] = slot2.peek();
		} catch (EmptyStackException e) {
			cards[1] = null;
		}
		try {
			cards[2] = slot3.peek();
		} catch (EmptyStackException e) {
			cards[2] = null;
		}

		return cards;
	}

	/**
	 * Getter for player's board production.
	 * @return the board production.
	 */
	public BoardProduction getBoardProduction() {
		return (BoardProduction) slots.get(Id.BOARD_PRODUCTION.getValue());
	}

	/**
	 * Getter for the warehouse.
	 * @return the player's warehouse.
	 */
	public List<Depot> getWarehouse() {
		return warehouse;
	}

	public void addSpecialDepot(int capacity, ResourceType type){
		Id id;
		if (warehouse.size()==3) {
			id = Id.S_DEPOT_1;
		} else  if (warehouse.size()==4) {
			id = Id.S_DEPOT_2;
		} else {
			throw new RuntimeException("Having more than two special depots should be impossible");
		}
		warehouse.add(new SpecialDepot(capacity, id, type));
	}

	/**
	 * Called to add special slots to the slot array. they only contain productions.
	 * @param cost the resource the special production requires.
	 */
	public void addSpecialSlot(Resource cost){
		Id id;
		if (slots.size()==3) {
			id = Id.S_SLOT_1;
		} else  if (slots.size()==4) {
			id = Id.S_SLOT_2;
		} else {
			throw new RuntimeException("Having more than two special productions should be impossible");
		}
		slots.add(new SpecialProduction(id, cost));
	}

	/**
	 * This method is called when we succeed in storing a resource taken from tempResource in a depot
	 * @param resource removes the stored resource from tempResources
	 */
	public void subFromTempResources(Resource resource){
		List<Resource> tempResources = getTempResources();

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
		//	update(TEMP_RESOURCES_UPDATE, null, tempResources);
	}

	/**
	 * Preemptively checks if tempResources contains the argument.
	 * Used to check that nothing has gone wrong with the messages.
	 * @param resource resource to check for.
	 * @return whether the resource is present or not.
	 */
	public boolean tempResourcesContains (Resource resource){
		List<Resource> tempResources = getTempResources();

		for(Resource temp : tempResources){
			if(temp.getResourceType() == resource.getResourceType()){
				if(temp.getQuantity() >= resource.getQuantity()){
					return true;
				}
			}
		}
		return false;
	}


	public void addToTempResources(ArrayList<Resource> resources){
		this.tempResources = resources;
	}

	public void dumpTempResources(){
		tempResources.clear();
	}

	public String getNickname() {
		return nickname;
	}

	public List<Resource> getTempResources() {
		return tempResources;
	}


	public ArrayList<DepotView> getVisibleWarehouse() {

		List<DepotView> list = new ArrayList<>();

		for (Depot depot: warehouse){
			list.add(new DepotView(depot));
		}
		return (ArrayList<DepotView>) list;
	}

	/**
	 * Creates the reduced, serializable version of the faith track that is sent to the client.
	 * @return the serializable FaithTrackView.
	 */
	public FaithTrackView getVisibleFaithTrack() {
		return new FaithTrackView(faithTrack.getFaithMarker(),faithTrack.getPopeFavorTiles());
	}

	/**
	 * Creates the reduced, serializable version of the strongbox that is sent to the client.
	 * @return the serializable StrongboxView.
	 */
	public StrongboxView getVisibleStrongbox(){
		return new StrongboxView(strongbox);
	}

	//TODO
	public ArrayList<SlotView> getVisibleSlots() {
		return null;
	}

	/**
	 * Calculates the player total victory points.
	 * @return the total victory points.
	 */
	public int getVictoryPoints(){
		int vp = 0;

		for (Slot slot : slots) {
			for (DevelopmentCard card : ((DevelopmentCardSlot) slot).getSlot()){
				vp += card.getVictoryPoints();
			}
		}

		vp += faithTrack.getVictoryPoints();

		for (LeaderCard card : leaderCards){
			if (card.isActive()) vp += card.getVictoryPoints();
		}

		for (Resource resource : getTotalResources()){
			vp += resource.getQuantity()%5;
		}

		return vp;


	}


}
