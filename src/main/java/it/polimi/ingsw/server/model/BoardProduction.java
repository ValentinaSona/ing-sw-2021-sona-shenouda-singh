package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.exception.NotSufficientResourceException;


import java.util.HashMap;

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
            // SERVER ERROR?
            return;
        }

        Resource cost = boardProduction.getProductionCost()[0];

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

        else confirmed = true;

        //if the player can activate the production but the output
        //resourcetype is a jolly before activating the power we have to ask
        //how the player wants to convert the jolly type
       // update(JOLLY_RESOURCE, null, new Resource(1, ResourceType.JOLLY));
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
