package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PlayerView {
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
    private FaithTrackView faithTrackView;

    private List<LeaderCard> leaderCards;

    /**
     * Infinite depot where the production outputs are stored.
     */
    private StrongboxView strongboxView;

    /**
     * Special production that turns 2 resources of any kind into one resource of any kind.
     */
    private List<SlotView> slots = new ArrayList<>();

    private List<DepotView> warehouse = new ArrayList<>();

    private List<Resource> tempResources;

    /** TODO: when is the player interaction for the initial resources made? May need to change type to resource
     ** TODO: likewise, when is the leaderCard interaction made? Doesn't the constructor need to have 2 or 4 as input?
     ** TODO: does it really need the views? Or are they added manually? Should be an array anyway no?
     * Constructor
     */

    public PlayerView(String nickname){

        // All the parameters are set to false until a method specifically activates them.
        this.nickname = nickname;
        isMyTurn = false;
        mainAction = false;
        strongboxView = new StrongboxView();

        warehouse.add(new DepotView(Id.DEPOT_1, null,1));
        warehouse.add(new DepotView(Id.DEPOT_2, null,2));
        warehouse.add(new DepotView(Id.DEPOT_3, null, 3));

        slots.add(new BoardProductionView(Id.BOARD_PRODUCTION, false));
        slots.add(new DevelopmentCardSlotView(Id.SLOT_1));
        slots.add(new DevelopmentCardSlotView(Id.SLOT_2));
        slots.add(new DevelopmentCardSlotView(Id.SLOT_3));

        faithTrackView = new FaithTrackView();
    }

    public PlayerView(Player player) {

        nickname = player.getNickname();
        isMyTurn = false;
        mainAction = false;

        strongboxView = new StrongboxView(player.getStrongbox());
        warehouse = player.getWarehouse().stream().map(DepotView::new).collect(Collectors.toList());
        // TODO need help to know how slots are handled
        faithTrackView = new FaithTrackView(player.getFaithTrack());

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public boolean isMainAction() {
        return mainAction;
    }

    public void setMainAction(boolean mainAction) {
        this.mainAction = mainAction;
    }

    public FaithTrackView getFaithTrackView() {
        return faithTrackView;
    }

    public void setFaithTrackView(FaithTrackView faithTrackView) {
        this.faithTrackView = faithTrackView;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public void setLeaderCards(List<LeaderCard> leaderCards) {
        this.leaderCards = leaderCards;
    }

    public StrongboxView getStrongboxView() {
        return strongboxView;
    }

    public void setStrongboxView(StrongboxView strongboxView) {
        this.strongboxView = strongboxView;
    }

    public List<SlotView> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotView> slots) {
        this.slots = slots;
    }

    public List<DepotView> getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(List<DepotView> warehouse) {
        this.warehouse = warehouse;
    }

    public List<Resource> getTempResources() {
        return tempResources;
    }

    public void setTempResources(List<Resource> tempResources) {
        this.tempResources = tempResources;
    }
}
