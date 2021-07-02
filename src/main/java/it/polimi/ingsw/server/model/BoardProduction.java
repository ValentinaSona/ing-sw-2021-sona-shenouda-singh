package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotSufficientResourceException;


import java.util.HashMap;

/**
 * Represents the board production and generates an equivalent one with the chosen resources.
 */
public class BoardProduction extends Slot {
    private Production boardProduction;
    private ResourceType chosenType;

    public BoardProduction(Id id){
        super(id);
        Resource[] cost = new Resource[]{new Resource(2, ResourceType.JOLLY)};
        Resource[] out = new Resource[]{new Resource(1, ResourceType.JOLLY)};
        this.boardProduction = new Production(cost, out);
    }

    @Override
    public void check(boolean card) throws NotSufficientResourceException {
        if(card){
            return;
        }

        Resource cost = boardProduction.getProductionCost()[0];

        // To activate the board production we can use resource of different types
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

        else confirmed = true;

    }

    /**
     * This method is called when a player confirm that he has a put all the resources for that production
     * and the method check is called and if the production produce a jolly the player has to call
     * this method to declare how he wants to convert that jolly
     * @param resource the resource type chosen for the output.
     */
    public void chooseJolly(ResourceType resource){
        this.chosenType = resource;
    }

    @Override
    public Resource[] productionCost(){
        return resourceCloset.toArray(new Resource[0]);
    }

    @Override
    public Resource[] activateProduction() {
        if(confirmed){
            Resource[] gained = new Resource[]{new Resource(1, chosenType)};
            confirmed = false;
            chosenType = null;
            resourceCloset.clear();
            originResourceHashMap.clear();
            return gained;
        }else {
            return null;
        }
    }
}
