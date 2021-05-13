package it.polimi.ingsw.server.controller;


import it.polimi.ingsw.server.exception.TwoLeaderCardsException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertWhiteMarblesMessage;

import java.util.ArrayList;

public class MarketController extends AbstractController {
    private static MarketController singleton;
    private final Market market;
    private final ResourceController resourceController;

    private MarketController(Model model){
        super(model);
        this.market = MarketBuilder.build();
        this.resourceController = ResourceController.getInstance(model);
    }

    public static MarketController getInstance(Model model){
        if(singleton == null){
            singleton = new MarketController(model);
        }

        return  singleton;

    }

    /**
     * Called by ClientBuyMarblesMessage.handleMessage(). Gets the marbles from the market triggering its reconfiguration
     * and handles the TwoLeaderCardsException. If it is not thrown, calls convertMarbles to acquire the gained resources.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void buyMarbles(ClientBuyMarblesMessage action, RemoteViewHandler view, User user) {

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) || !(player.getMainAction()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        }else{

            try {
                player.toggleMainAction();
                MarketMarble[] marbles = market.getResources(player, action.getRowCol());

                ArrayList<Resource> resources = convertMarbles(marbles);
                player.addToTempResources(resources);
                view.handleStatusMessage(StatusMessage.CONTINUE);

            } catch (TwoLeaderCardsException e) {
                //the client knows that if he receive this type of message while doing
                //this action he has to choose between his leaderCards
                view.handleStatusMessage(StatusMessage.CONTINUE_EXCEPTION);
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
    public void convertWhiteMarbles(ClientConvertWhiteMarblesMessage action, RemoteViewHandler view, User user) {

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            MarketMarble[] marbles = market.getChosen(action.getChoices());
            ArrayList<Resource> resources = convertMarbles(marbles);
            player.addToTempResources(resources);
            // the client knows that if he receive this type of message while doing
            // this action he has to go on to deposit its resources.
            view.handleStatusMessage(StatusMessage.CONTINUE);
        }
    }


    /**
     * Called if the course of the action has been interrupted by the TwoLeaderCardsException to finish the conversion of the resources.
     * @param action the ClientMessage containing information about the player's action.
     * @param view the player's corresponding RemoteViewHandler that will handle status messages to be sent back to the view.
     * @param user the User corresponding to the player making the action.
     */
    public void convertMarbles(ClientConvertMarblesMessage action, RemoteViewHandler view, User user) {

        Player player = getModel().getPlayerFromUser(user);

        if( !(player.getTurn()) ){
            view.handleStatusMessage(StatusMessage.CLIENT_ERROR);
        } else {

            ArrayList<Resource> resources = convertMarbles(action.getMarbles());
            player.addToTempResources(resources);
            // the client knows that if he receive this type of message while doing
            // this action he has to go on to deposit its resources.
            view.handleStatusMessage(StatusMessage.CONTINUE);
        }


    }

    /**
     * Called on the definitive array of gained market marbles.
     * Returns an array of market marbles into an array of resources.
     * If faith has been collected, it issues a call to the ResourcesController to add the faith to the user's faith track.
     * @param marbles marbles to be collected
     * @return ArrayList of resources, without faith included, to be deposited into the warehouse.
     */
    private ArrayList<Resource> convertMarbles(MarketMarble[] marbles) {
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
            resourceController.addFaithPoints(getCurrentPlayer(), faithPoints);
        }
        return temp;
    }

    /**
     * This method is called by the LeaderCardsController when the conditions to activate
     * a leaderCard are verified.
     * @param player the Player activating the ability.
     * @param marble the marble type granted by the ability.
     */
    public void addMarketAbility(Player player, MarketMarble marble){
        market.addAbility(marble, player);
    }
}