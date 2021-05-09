package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.DevelopmentCardException;
import it.polimi.ingsw.server.exception.NotSufficientResourceException;
import java.util.HashMap;
import java.util.Stack;

public class DevelopmentCardSlot extends Slot{

    private final Stack<DevelopmentCard> slot = new Stack<>();
    private static final int MAX_LENGTH = 3;
    private DevelopmentCard targetCard;
    private int row;
    private int col;

    public DevelopmentCardSlot(Id id){
        super(id);
    }

    public DevelopmentCard peek(){
        return slot.peek();
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

    public void setTargetCard(DevelopmentCard targetCard, int row, int col) throws DevelopmentCardException {
        DevelopmentCard lastInsertion = peek();

        if((lastInsertion.getLevel() == targetCard.getLevel() -1) && slot.size() <= MAX_LENGTH){
            //we can put the card in this slot
            this.targetCard = targetCard;
            this.row = row;
            this.col = col;
            update(TARGET_CARD, null, targetCard);
        }else{
            //we can't put the card in this slot
            update(DEVEL_CARD_LEVEL_ERROR, null, null);
            throw new DevelopmentCardException();
        }
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    @Override
    public void check(boolean card) throws NotSufficientResourceException {
        Resource[] cost;
        if(card){
            cost = targetCard.getCost();
            //faccio il check su sconti sul costo della carta
        }else {
            cost = slot.peek().getProduction().getProductionCost();
        }

        if( cost.length == resourceCloset.size()){
            for(Resource res : cost){
                if(!resourceCloset.contains((Resource) res)){
                    //if the resourceCloset doesn't contains the required resource
                    //the action is denied
                    resourceCloset.clear();
                    //returning a map to the controller that will deposit the  resources in the right places
                    HashMap<Id, Resource> copy = (HashMap<Id, Resource>) originResourceHashMap.clone();
                    originResourceHashMap.clear();
                    throw new NotSufficientResourceException(copy);
                }
            }
        }else{
            resourceCloset.clear();
            HashMap<Id, Resource> copy = (HashMap<Id, Resource>) originResourceHashMap.clone();
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

    public Stack<DevelopmentCard> getSlot() { return slot; }
}
