package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.DevelopmentCardException;
import it.polimi.ingsw.server.exception.NotSufficientResourceException;


import java.util.HashMap;
import java.util.Stack;

public class DevelopmentCardSlot extends Slot {

    private final Stack<DevelopmentCard> slot = new Stack<>();
    private static final int MAX_LENGTH = 3;
    private DevelopmentCard targetCard;
    private int row;
    private int col;

    public DevelopmentCardSlot(Id id){
        super(id);
    }

    public DevelopmentCard peek(){

        if (!slot.empty()) return slot.peek();
        else return null;
    }

    public void push(DevelopmentCard developmentCard){
        int newLevel = developmentCard.getLevel();
        int oldLevel = slot.peek().getLevel();

        if(slot.size() < MAX_LENGTH && newLevel == oldLevel+1)
            slot.push(developmentCard);
        else
            throw new RuntimeException("I can't put more thant 3 DevelopmentCards or level incoherence");
    }

    public DevelopmentCard pop(){
        return slot.pop();
    }

    /** TODO: Remove updates for new methods.
     * Checks that the card selected can be placed into the slot. If yes, its coordinates are saved, otherwise throws an exception.
     * @param targetCard Card to be placed into the slot.
     * @param row Market row coordinate of the card.
     * @param col Market column coordinate of the card.
     * @throws DevelopmentCardException The card cannot be placed on top of this slot.
     */
    public void setTargetCard(DevelopmentCard targetCard, int row, int col) throws DevelopmentCardException {
        DevelopmentCard lastInsertion = peek();

        if( ((lastInsertion == null && targetCard.getLevel() == 1 ) ||  ( lastInsertion != null && lastInsertion.getLevel() == targetCard.getLevel() -1)) && slot.size() <= MAX_LENGTH && id != Id.S_SLOT_1 && id != Id.S_SLOT_2){
            //we can put the card in this slot
            this.targetCard = targetCard;
            this.row = row;
            this.col = col;
            //update(TARGET_CARD, null, targetCard);
        }else{
            //we can't put the card in this slot
            //update(DEVEL_CARD_LEVEL_ERROR, null, null);
            throw new DevelopmentCardException();
        }
    }

    /**
     * Can handle both the cost of the card that is being placed into the slot and of the slot's top production.
     * Called when resources have been placed in the resourceCloset to pay for either one, and checks whether the player has supplied enough resources.
     * @param card true if the cost is to buy the card, false if it's to activate a production.
     * @throws NotSufficientResourceException the player has not placed enough resources into the closet.
     */
    @Override
    public void check(boolean card) throws NotSufficientResourceException {
        Resource[] cost;

        if(card){
            cost = targetCard.getCost();
        }else {
            cost = slot.peek().getProduction().getProductionCost();
        }

        if( cost.length == resourceCloset.size()){
            for(Resource res : cost){
                if(!resourceCloset.contains(res)){
                    //if the resourceCloset doesn't contains the required resource
                    //the action is denied
                    resourceCloset.clear();
                    //returning a map to the controller that will deposit the  resources in the right places
                    HashMap<Id, Resource> copy = new HashMap<>(originResourceHashMap);
                    originResourceHashMap.clear();
                    throw new NotSufficientResourceException(copy);
                }
            }
        }else{
            resourceCloset.clear();
            HashMap<Id, Resource> copy = new HashMap<>(originResourceHashMap);
            originResourceHashMap.clear();
            throw new NotSufficientResourceException(copy);
        }
        confirmed = true;

    }


    public void buyDevelopmentCard(){

        if(confirmed) {
            //if check doesn't throw any exception we can buy the card
            confirmed = false;
            resourceCloset.clear();
            originResourceHashMap.clear();
            push(targetCard);

            targetCard = null;
        }

    }

    @Override
    public Resource[] activateProduction(){
        if(confirmed){
            DevelopmentCard activateDev = slot.peek();
            confirmed = false;
            resourceCloset.clear();
            originResourceHashMap.clear();
            return activateDev.getProduction().getProductionOut();
        }else {
            return null;
        }


    }

    /**
     * Getter for the Target card's market row.
     * @return int representing the row.
     */
    public int getRow(){
        return row;
    }
    /**
     * Getter for the Target card's market column.
     * @return int representing the column.
     */
    public int getCol(){
        return col;
    }

    /**
     * Getter for the card stack, needed for iterating through it without deleting cards.
     * @return the slot's stack of development cards.
     */
    public Stack<DevelopmentCard> getSlot() { return slot; }
}
