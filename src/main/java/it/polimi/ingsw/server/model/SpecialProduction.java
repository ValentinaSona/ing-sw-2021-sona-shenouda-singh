package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotSufficientResourceException;


import java.util.HashMap;
//TODO replace  update() calls with notify() of the lambdaObservable class

public class SpecialProduction extends Slot {
    private Production specialProduction;
    private ResourceType chosenType;

    public SpecialProduction(Id id, Resource productionCost){
        super(id);
        this.chosenType = productionCost.getResourceType();
        Resource[] cost = new Resource[]{productionCost};
        Resource[] out = new Resource[]{new Resource(1, ResourceType.JOLLY), new Resource(1, ResourceType.FAITH)};
        this.specialProduction = new Production(cost, out);
    }

    @Override
    public void check(boolean card) throws NotSufficientResourceException {
        if(card){
            //this method should never be called with card = true
            throw new RuntimeException("Super error");
        }

        Resource cost = specialProduction.getProductionCost()[0];

        //to activate the board production we can use resource of different types
        int tot = 0;
        for(Resource res : resourceCloset){
            tot += res.getQuantity();
        }

        if( cost.getQuantity() != tot){
            resourceCloset.clear();
            HashMap<Id, Resource> copy = new HashMap<>(originResourceHashMap);
            originResourceHashMap.clear();
            throw new NotSufficientResourceException(copy);
        }
    }

    /**
     * This method is called when a player confirm that he has a put all the resources for that production
     * and the method check is called and if the production produce a jolly the player has to call
     * this method to declare how he wants to convert that jolly
     * @param resource
     */
    public void chooseJolly(ResourceType resource){
        this.chosenType = resource;
    }

    @Override
    public Resource[] activateProduction() {
        if(confirmed){
            Resource[] gained = new Resource[]{new Resource(1, chosenType), new Resource(1, ResourceType.FAITH)};
            confirmed = false;
            chosenType = null;
            resourceCloset.clear();
            originResourceHashMap.clear();
            return gained;
        }else {
            return null;
        }
    }
    @Override
    public Resource[] productionCost(){
        return new Resource[]{new Resource(1, chosenType), new Resource(1, ResourceType.FAITH)};
    }

    public ResourceType getChosenType() {
        return chosenType;
    }
}