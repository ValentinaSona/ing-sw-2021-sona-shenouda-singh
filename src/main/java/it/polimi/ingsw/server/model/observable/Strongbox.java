package it.polimi.ingsw.server.model.observable;

import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObservable;

import static it.polimi.ingsw.server.model.ResourceType.*;
//TODO replace  update() calls with notify() of the lambdaObservable class
public class Strongbox extends LambdaObservable<Transmittable> {

    private final Resource shield;
    private final Resource coin;
    private final Resource stone;
    private final Resource servant;

    /**
     * Constructor used at the start of a game. Sets all resources to 0.
     */
    public Strongbox(){
        this.shield = new Resource(0, SHIELD);
        this.coin = new Resource(0, COIN);
        this.stone = new Resource(0, STONE);
        this.servant = new Resource(0, SERVANT);

    }

    /**
     * Constructor used when restarting in the middle of a game.
     * @param shield shields in the strongbox.
     * @param coin coins in the strongbox.
     * @param stone stones in the strongbox.
     * @param servant servants in the strongbox.
     */
    public Strongbox(Resource shield, Resource coin, Resource stone, Resource servant){
        if (shield.getResourceType() == SHIELD) {
            this.shield = shield;
        } else {throw new RuntimeException("Wrong argument order!");}
        if (coin.getResourceType() == COIN) {
            this.coin= coin;
        } else {throw new RuntimeException("Wrong argument order!");}
        if (stone.getResourceType() == STONE) {
            this.stone= stone;
        } else {throw new RuntimeException("Wrong argument order!");}
        if (servant.getResourceType() == SERVANT) {
            this.servant = servant;
        } else {throw new RuntimeException("Wrong argument order!");}

    }

    /**
     * Function that returns all resources present in the strongbox.
     * @return array of all resources present in the strongbox.
     */
    public Resource[] getAvailableResources(){
        return new Resource[]{shield, coin, stone, servant};
    }

    /**
     * Function that returns a type of resource from the strongbox.
     * @param resourceType resourceType of the resource to be retrieved.
     * @return resource of the input resourceType stored in the strongbox.
     */
    public Resource getAvailableResources(ResourceType resourceType){
        switch (resourceType){
            case SERVANT:
                return servant;
            case COIN:
                return coin;
            case SHIELD:
                return shield;
            case STONE:
                return stone;
            default:
                throw new RuntimeException("This type of resource is not available in the strongbox");
        }
    }

    /**
     * Function that adds a single resource to the strongbox.
     * @param resource resource to be added to the strongbox.
     */
    public void addResources(Resource resource){
        switch (resource.getResourceType()){
            case SERVANT:
                this.servant.add(resource);
                break;
            case COIN:
                this.coin.add(resource);
                break;
            case SHIELD:
                this.shield.add(resource);
                break;
            case STONE:
                this.stone.add(resource);
                break;
            default:
                throw new RuntimeException("This type of resource is not available in the strongbox");
        }
    }

    /**
     * Function that adds multiple resources to the strongbox.
     * @param resources array of resources to be added to the strongbox.
     */
    public void addResources(Resource[] resources){
        for (Resource resource : resources) {
            switch (resource.getResourceType()) {
                case SHIELD:
                    this.shield.add(resource);
                    break;
                case COIN:
                    this.coin.add(resource);
                    break;
                case STONE:
                    this.stone.add(resource);
                    break;
                case SERVANT:
                    this.servant.add(resource);
                    break;
                default:
                    throw new RuntimeException("This type of resource is not available in the strongbox");
            }
        }
    }


    public void subResources(Resource resource){
        switch (resource.getResourceType()){
            case SERVANT:
                this.servant.sub(resource);
                break;
            case COIN:
                this.coin.sub(resource);
                break;
            case SHIELD:
                this.shield.sub(resource);
                break;
            case STONE:
                this.stone.sub(resource);
                break;
            default:
                throw new RuntimeException("This type of resource is not available in the strongbox");
        }
    }


    public void subResources(Resource[] resources){
        for (Resource resource : resources) {
            switch (resource.getResourceType()) {
                case SHIELD:
                    this.shield.sub(resource);
                    break;
                case COIN:
                    this.coin.sub(resource);
                    break;
                case STONE:
                    this.stone.sub(resource);
                    break;
                case SERVANT:
                    this.servant.sub(resource);
                    break;
                default:
                    throw new RuntimeException("This type of resource is not available in the strongbox");
            }
        }
    }
}
