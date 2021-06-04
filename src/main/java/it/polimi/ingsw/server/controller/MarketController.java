package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.TwoLeaderCardsException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertWhiteMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerBoughtMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerChooseWhiteMarblesMessage;

import java.util.ArrayList;
import java.util.List;

public class MarketController{
    private static MarketController singleton;
    private final ResourceController resourceController;
    private final Game model;

    private MarketController(Game model){
        this.model = model;
        this.resourceController = ResourceController.getInstance(model);
    }

    public static MarketController getInstance(Game model){
        if(singleton == null){
            singleton = new MarketController(model);
        }

        return  singleton;

    }


    public static MarketController destroy(){
        if(singleton != null){
            singleton = null;
        }
        return null;
    }

    /**
     * Called by ClientBuyMarblesMessage.handleMessage(). Gets the marbles from the market triggering its reconfiguration
     * and handles the TwoLeaderCardsException. If it is not thrown, calls convertMarbles to acquire the gained resources.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void buyMarbles(ClientBuyMarblesMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ||
                model.getGameState() != GameState.PLAY ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        }else{

            try {
                Market market = model.getMarketInstance();
                player.toggleMainAction();
                MarketMarble[] marbles = market.getResources(player, action.getRowCol());

                ArrayList<Resource> resources = (ArrayList<Resource>) convertMarbles(marbles);
                player.addToTempResources(resources);

                //TODO: should also send faithTrack;
                model.notify(new ServerBoughtMarblesMessage(market.getVisible(),
                        resources,
                        model.getUserFromPlayer(player)));

            } catch (TwoLeaderCardsException e) {
                //the client knows that if he receive this type of message while doing
                //this action he has to choose between his leaderCards
                model.notify(new ServerChooseWhiteMarblesMessage(e.getWhiteMarbles(),
                        model.getUserFromPlayer(player)));
            }
        }
    }

    /**
     * Called when the player has been prompted to choose which resources he wishes to convert his white marbles in as result of twoLeaderCardsException.
     * Takes as an input an array of MarketMarbles that it asks the market to substitute to the white marbles in the previous array.
     * Passes the new array to convertMarbles, returning to the normal control flow.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void convertWhiteMarbles(ClientConvertWhiteMarblesMessage action, RemoteViewHandler view, User user) throws EndOfGameException {

        Player player = model.getPlayerFromUser(user);

        if( !(player.getTurn()) ||
                model.getGameState() != GameState.PLAY  ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {
            Market market = model.getMarketInstance();
            player.toggleMainAction();

            MarketMarble[] marbles = market.getChosen(action.getChoices());

            ArrayList<Resource> resources = (ArrayList<Resource>) convertMarbles(marbles);
            player.addToTempResources(resources);
            // the client knows that if he receive this type of message while doing
            // this action he has to go on to deposit its resources.
            model.notify(new ServerBoughtMarblesMessage(market.getVisible(),
                    resources,
                    model.getUserFromPlayer(player)));
        }
    }

    /**
     * Called on the definitive array of gained market marbles.
     * Returns an array of market marbles into an array of resources.
     * If faith has been collected, it issues a call to the ResourcesController to add the faith to the user's faith track.
     * @param marbles marbles to be collected
     * @return ArrayList of resources, without faith included, to be deposited into the warehouse.
     */
    private List<Resource> convertMarbles(MarketMarble[] marbles) throws EndOfGameException {
        ArrayList<Resource> temp = new ArrayList<>();
        Resource faithPoints = null;

        for(ResourceType type : ResourceType.values()){
            int count = 0;
            for(MarketMarble marble : marbles){
                if(marble.convertToResource() == type)
                    count++;
            }
            if(count != 0)
                temp.add(new Resource(count, type));
        }


        //control if there is a faith resource
        for(Resource res : temp){
            if(res.getResourceType() == ResourceType.FAITH){
                faithPoints = res;
                temp.remove(res);
                break;
            }
        }

        //if faithPoints is different from null
        if(faithPoints != null){
            resourceController.addFaithPoints(model.getCurrentPlayer(), faithPoints);
        }
        return temp;
    }

    /**
     * This method is called by the LeaderCardsController when the conditions to activate
     * a leaderCard are verified.
     * @param player the Player activating the ability.
     * @param marbleAbility the  ability.
     */
    public void addMarketAbility(Player player, WhiteMarbleAbility marbleAbility){
        model.setMarketInstance(model.getMarketInstance().addAbility(marbleAbility.getMarble(), player));
    }
}