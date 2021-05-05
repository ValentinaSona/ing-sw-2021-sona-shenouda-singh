package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.DevelopmentCardException;
import it.polimi.ingsw.exception.NotSufficientResourceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DevelopmentCardSlot extends AbstractModel{

    private final Stack<DevelopmentCard> slot = new Stack<>();
    private final Origin id;
    private static final int MAX_LENGTH = 3;
    private DevelopmentCard targetCard;
    private final HashMap<Origin, Resource> originResourceHashMap = new HashMap<>();
    private final ArrayList<Resource> resourceCloset = new ArrayList<>();
    private boolean confirmed;

    public DevelopmentCardSlot(Origin id){
        this.id = id;
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

    public void setResourceCloset(Resource resource, Origin origin){
        //the map make it possible to understand from where we are taking the resource
        //for buying or activating a developmentCard
        this.originResourceHashMap.put(origin, resource);

        for(Resource res : resourceCloset){
            if(res.getResourceType() == resource.getResourceType()){
                res.add(resource);
                return;
            }
        }
        resourceCloset.add(resource);
    }

    public void setTargetCard(DevelopmentCard targetCard) throws DevelopmentCardException {
        DevelopmentCard lastInsertion = peek();

        if((lastInsertion.getLevel() == targetCard.getLevel() -1) && slot.size() <= MAX_LENGTH){
            //we can position the card in this slot
            this.targetCard = targetCard;
        }else{
            //we can't position the card in this slot
            update(DEVEL_CARD_LEVEL_ERROR, null, null);
            throw new DevelopmentCardException();
        }
    }

    public void check() throws NotSufficientResourceException {
        Resource[] productionCost = slot.peek().getProduction().getProductionCost();

        if( productionCost.length == resourceCloset.size()){
            for(Resource res : productionCost){
                if(!resourceCloset.contains((Resource) res)){
                    //if the resourceCloset doesn't contains the required resource
                    //the action is denied
                    resourceCloset.clear();
                    //returning a map to the controller that will deposit the  resources in the right places
                    HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
                    originResourceHashMap.clear();
                    throw new NotSufficientResourceException(copy);
                }
            }
        }else{
            resourceCloset.clear();
            HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
            originResourceHashMap.clear();
            throw new NotSufficientResourceException(copy);
        }
        confirmed = true;

    }

    public boolean isConfirmed(){
        return confirmed;
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
