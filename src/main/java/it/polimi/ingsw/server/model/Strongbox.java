package it.polimi.ingsw.server.model;

import it.polimi.ingsw.client.modelview.StrongboxView;
import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;

import static it.polimi.ingsw.server.model.ResourceType.*;

public class Strongbox{

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

    public Strongbox(StrongboxView strongboxView) {
        this.shield = strongboxView.getShield();
        this.coin = strongboxView.getCoin();
        this.stone = strongboxView.getStone();
        this.servant = strongboxView.getServant();
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
    public void addResources(Resource resource) throws InvalidDepotException {
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
                throw new InvalidDepotException();
        }
    }

    /**
     * Function that adds multiple resources to the strongbox.
     * @param resources array of resources to be added to the strongbox.
     */
    public void addResources(Resource[] resources) throws InvalidDepotException {
        for (Resource resource : resources) {
            if (resource==null) continue;

            switch (resource.getResourceType()) {
                case SHIELD -> this.shield.add(resource);
                case COIN -> this.coin.add(resource);
                case STONE -> this.stone.add(resource);
                case SERVANT -> this.servant.add(resource);
                default -> throw new InvalidDepotException();
            }
        }
    }


    public void subResources(Resource resource) throws InvalidDepotException {
       try {
           switch (resource.getResourceType()) {
               case SERVANT -> this.servant.sub(resource);
               case COIN -> this.coin.sub(resource);
               case SHIELD -> this.shield.sub(resource);
               case STONE -> this.stone.sub(resource);
               default -> throw new InvalidDepotException();
           }
       } catch (RuntimeException e){
           throw new InvalidDepotException();
       }
    }


    public void subResources(Resource[] resources){
        for (Resource resource : resources) {
            switch (resource.getResourceType()) {
                case SHIELD -> this.shield.sub(resource);
                case COIN -> this.coin.sub(resource);
                case STONE -> this.stone.sub(resource);
                case SERVANT -> this.servant.sub(resource);
                default -> throw new RuntimeException("This type of resource is not available in the strongbox");
            }
        }
    }
}
