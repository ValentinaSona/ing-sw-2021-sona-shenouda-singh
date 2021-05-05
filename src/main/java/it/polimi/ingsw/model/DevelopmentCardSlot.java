package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.NotSufficientResourceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DevelopmentCardSlot {

    private final Stack<DevelopmentCard> slot = new Stack<>();
    private final int id;
    private static final int MAX_LENGTH = 3;
    private final HashMap<Origin, Resource> originResourceHashMap = new HashMap<>();
    private final ArrayList<Resource> activationResources = new ArrayList<>();
    private final ArrayList<Resource> buyResources = new ArrayList<>();
    private boolean activateProduction;
    private boolean confirmBuy;

    public DevelopmentCardSlot(int id){
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

    /**
     * The ResourceController set tempResources of the slot when a player selectThatProduction
     * in this way when the player decide to activate all the selected production the resources are already available
     * @param resource
     */
    public void setTempResourcesProduction(Resource resource, Origin origin){
        this.originResourceHashMap.put(origin, resource);
        for(Resource res : activationResources){
            if(res.getResourceType() == resource.getResourceType()){
                res.add(resource);
                return;
            }
        }
        activationResources.add(resource);
    }

    public void setTempResourcesBuying(Resource resource, Origin origin){
        this.originResourceHashMap.put(origin, resource);
        for(Resource res : buyResources){
            if(res.getResourceType() == resource.getResourceType()){
                res.add(resource);
                return;
            }
        }
        buyResources.add(resource);
    }
    public void checkProduction() throws NotSufficientResourceException {
        Resource[] cost = slot.peek().getProduction().getProductionCost();

        if( cost.length == activationResources.size()){
            for(Resource res : cost){
                if(!activationResources.contains((Resource) res)){
                    activationResources.clear();
                    HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
                    originResourceHashMap.clear();
                    throw new NotSufficientResourceException(copy);
                }
            }
        }else{
            activationResources.clear();
            HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
            originResourceHashMap.clear();
            throw new NotSufficientResourceException(copy);
        }
        activateProduction = true;

    }

    public void checkBuying(DevelopmentCard card) throws NotSufficientResourceException{
        Resource[] cost = card.getCost();

        if( cost.length == buyResources.size()){
            for(Resource res : cost){
                if(!buyResources.contains((Resource) res)){
                    buyResources.clear();
                    HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
                    originResourceHashMap.clear();
                    throw new NotSufficientResourceException(copy);
                }
            }
        }else{
            buyResources.clear();
            HashMap<Origin, Resource> copy = (HashMap<Origin, Resource>) originResourceHashMap.clone();
            originResourceHashMap.clear();
            throw new NotSufficientResourceException(copy);
        }
        confirmBuy = true;


    }

    public boolean isActivateProduction(){
        return activateProduction;
    }

    public boolean isConfirmBuy() {
        return confirmBuy;
    }

    public boolean buyDevelopmentCard(){
        if(confirmBuy){
            confirmBuy = false;
            buyResources.clear();
            originResourceHashMap.clear();
            return true;
        }
        return false;
    }

    public Resource[] activateProduction(){
        DevelopmentCard activateDev = slot.peek();

        if(activateProduction){
            activateProduction = false;
            activationResources.clear();
            originResourceHashMap.clear();
            return activateDev.getProduction().getProductionOut();
        }

        throw new RuntimeException("You should  not call this method if activateProduction is not true");
    }

    public Stack<DevelopmentCard> getSlot() { return slot; }
}
