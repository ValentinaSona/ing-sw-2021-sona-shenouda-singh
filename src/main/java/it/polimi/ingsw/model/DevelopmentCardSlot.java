package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Origin;
import it.polimi.ingsw.exception.NotSufficientResourceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DevelopmentCardSlot {
    private final Stack<DevelopmentCard> slot = new Stack<>();
    private final int id;
    private static final int MAX_LENGTH = 3;
    private final HashMap<Origin, Resource> tempResources = new HashMap<>();
    private final ArrayList<Resource> activationResources = new ArrayList<>();

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
            throw new RuntimeException("I can't put more thant 3 DevelopmentCards or level incoerence");
    }

    public DevelopmentCard pop(){
        return slot.pop();
    }

    /**
     * The ResourceController set tempResources of the slot when a player selectThatProduction
     * in this way when the player decide to activate all the selected production the resources are already available
     * @param resource
     */
    public void setTempResources(Resource resource, Origin origin){
        tempResources.put(origin, resource);
        for(Resource res : activationResources){
            if(res.getResourceType() == resource.getResourceType()){
                res.add(resource);
                return;
            }
        }

        activationResources.add(resource);
    }

    public void check() throws NotSufficientResourceException {
        Resource[] cost = slot.peek().getProduction().getProductionCost();

        if( cost.length == activationResources.size()){
            for(Resource res : cost){
                if(!activationResources.contains((Resource) res)){
                    activationResources.clear();
                    throw new NotSufficientResourceException(tempResources);
                }
            }
        }else{
            activationResources.clear();
            throw new NotSufficientResourceException(tempResources);
        }
    }

    public Resource[] activateProduction(){
        DevelopmentCard activateDev = slot.peek();

        return activateDev.getProduction().getProductionOut();
    }
}
