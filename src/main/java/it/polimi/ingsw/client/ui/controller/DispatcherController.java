package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLIMessageHandler;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.*;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;
import it.polimi.ingsw.utils.observer.LambdaObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Single class through which all messages are received.
 * The messages call the various methods of this class to be handled.
 * After updating the view with the needed/new data, they are forwarded to CLIMessageHandler or GUIMessageHandler for UI specific handling.
 */
public class DispatcherController implements Runnable, LambdaObserver {
    private static DispatcherController singleton;
    /**
     * Used by the GUI to reference its various controllers.
     */
    private UiControllerInterface currentController;
    /**
     * Queue containing the server messages to be handled.
     */
    private final BlockingQueue<Transmittable> serverMessages= new LinkedBlockingDeque<>();
    private final boolean gui;

    public static DispatcherController getInstance(boolean gui){
        if(singleton == null){
            singleton = new DispatcherController(gui);
        }

        return singleton;
    }

    /** Method to use only when the class has already been created
     * @return the already initialized instance of the class.
     */
    public static DispatcherController getInstance() {
        return singleton;
    }

    /**
     * @param gui whether it should forward to gui or cli.
     */
    private DispatcherController(boolean gui){
        this.gui = gui;
    }

    public void setCurrentController(UiControllerInterface currentController) {
        this.currentController = currentController;
    }

    public UiControllerInterface getCurrentController() {
        return currentController;
    }

    /**
     * The dispatcher controller observes the connection to retrieve messages.
     * @param serverMessage the retrieved server message to be added to the queue.
     */
    public void update(Transmittable serverMessage){
        try{
            this.serverMessages.put(serverMessage);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        while (true){
            try{
                Transmittable message = this.serverMessages.take();
                if(message instanceof StatusMessage){
                    this.handleStatus((StatusMessage)message);
                }else{
                    ((ClientHandleable) message).handleMessage(this);
                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }



    public void handleFinalScore(ServerFinalScoreMessage message){


        if(gui){
            GUIMessageHandler.getInstance().handleServerFinalScoreMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerFinalScoreMessage(message);
        }
    }

    public void handleSoloDiscard(ServerSoloDiscardMessage message){
        GameView.getInstance().setDevelopmentCardsMarket(message.getDevView());
        if(gui){
            GUIMessageHandler.getInstance().handleServerServerSoloDiscardMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerServerSoloDiscardMessage(message);
        }
    }

    public void handleSoloMove(ServerSoloMoveMessage message){
        GameView.getInstance().setBlackCross(message.getBlackCross());

        if(gui){
            GUIMessageHandler.getInstance().handleServerSoloMoveMessage(message);

        }else{
            CLIMessageHandler.getInstance().handleServerSoloMoveMessage(message);
        }
    }



    public void handleSetupGame(ServerSetupGameMessage message){
        GameView.getInstance(message.getUsers());
        GameView.getInstance().setMarketInstance(message.getMarketView());
        GameView.getInstance().setDevelopmentCardsMarket(message.getDevMarketView());



        if(gui){
            GUIMessageHandler.getInstance().handleSetupGameMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerSetupGameMessage(message);
        }
    }

    public void handleUpdateLobby(ServerUpdateLobbyMessage message){

        MatchSettings.getInstance().setTotalUsers(message.getNumOfPlayer());
        if(gui){
            GUIMessageHandler.getInstance().handleUpdateLobbyMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleUpdateLobbyMessage(message);
        }
    }

    /**
     * This message is received by ALL users. Only the user indicated in the message proceeds to handle its contents.
     * This method updates the player's warehouse before passing the message to the UI for handling.
     * @param message handleable.
     */
    public void handleSetupAction(ServerSetupActionMessage message){

        GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouseView());
        if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())){
            // Other players don't know the leader cards
            GameView.getInstance().getPlayerFromUser(message.getUser()).setLeaderCards(message.getChosen());
        }

        if(gui){
            GUIMessageHandler.getInstance().handleSetupActionMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerSetupActionMessage(message);
        }
    }

    /**
     * This message is received by ALL users. Only the user indicated in the message proceeds to handle its contents.
     * This method updates the player's faith track before passing the message to the UI for handling.
     * @param message contains resource number, 4 leader cards, a faith track and the addressed user.
     */
    public void handleSetupUser(ServerSetupUserMessage message){
       GameView.getInstance().getPlayerFromUser(message.getUser()).setFaithTrackView(message.getFaithTrackView());

        if(gui){

            synchronized (DispatcherController.getInstance()) {

                while(GUIHelper.getInstance().getCurrentScreen() != ScreenName.STARTING_CHOICE) {
                    try {
                        DispatcherController.getInstance().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                GUIMessageHandler.getInstance().handleServerSetupUserMessage(message);
            }
        }else {
            CLIMessageHandler.getInstance().handleServerSetupUserMessage(message);
        }
    }

    /**
     * This message is received by ALL users. Only the user indicated as starting in the message proceeds to handle its contents.
     * This method contains references to the players starting and ending their turn and sets the relative parameters.
     * @param message Contains two users.
     */
    public void handleStartTurn(ServerStartTurnMessage message){
        // Change current player reference
        GameView.getInstance().setCurrentPlayer(GameView.getInstance().getPlayerFromUser(message.getStartingTurn()));
        // Set starting player to have their turn and main action.
        GameView.getInstance().getPlayerFromUser(message.getStartingTurn()).setMyTurn(true);
        GameView.getInstance().getPlayerFromUser(message.getStartingTurn()).setMainAction(true);
        // Make sure the ending player turn ends.
        if (!MatchSettings.getInstance().isSolo() && !message.getStartingTurn().equals(message.getEndingTurn())) {
            GameView.getInstance().getPlayerFromUser(message.getEndingTurn()).setMyTurn(false);
            GameView.getInstance().getPlayerFromUser(message.getEndingTurn()).setMainAction(false);
        }

        if(gui){
            GUIMessageHandler.getInstance().handleServerStartTurnMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerStartTurnMessage(message);
        }
    }

    /**
     * Handles updates to the warehouse. Returned after tidy warehouse.
     * @param message contains warehouse and corresponding user.
     */
    public void handleWarehouse(ServerWarehouseMessage message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouseView());

        if(gui){
            GUIMessageHandler.getInstance().handleServerWarehouseMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerWarehouseMessage(message);
        }
    }


    public void handleBoughtMarbles(ServerBoughtMarblesMessage message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setTempResources(message.getBoughtResources());
        GameView.getInstance().setMarketInstance(message.getMarketView());
        GameView.getInstance().getCurrentPlayer().setMainAction(false);

        if(gui){
            GUIMessageHandler.getInstance().handleServerBoughtMarblesMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerBoughtMarblesMessage(message);
        }

    }


    public void handleThrowResource(ServerThrowResourceMessage message){
        if(gui){
            GUIMessageHandler.getInstance().handleServerThrowResourceMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerThrowResourceMessage(message);
        }
    }


    /**
     * Notifies of changes to any players faith track.
     * @param message the message to be handled.
     */
    public void handleFaithTrackMessage(ServerFaithTrackMessage message){
        if (message.getUser()==null)
            GameView.getInstance().addBlackCross(message.getFaith());
        else GameView.getInstance().getPlayerFromUser(message.getUser()).setFaithTrackView(message.getFaithTrackView());

        if(gui){
            GUIMessageHandler.getInstance().handleServerFaithTrackMessage (message);
        }else {
            CLIMessageHandler.getInstance().handleServerFaithTrackMessage (message);
        }
    }


    public void handleDepositAction(ServerDepositActionMessage  message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setTempResources(message.getTempResources());
        GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouseView());

        if(gui){
            GUIMessageHandler.getInstance().handleServerDepositActionMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerDepositActionMessage(message);
        }
    }


    public void handleDepositIntoSlot(ServerDepositIntoSlotMessage message){

        GameView.getInstance().getPlayerFromUser(message.getUser()).setStrongboxView(message.getStrongBoxView());
        GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouseView());
        GameView.getInstance().getPlayerFromUser(message.getUser()).setSlots(message.getSlotViews());

        if(gui){
            GUIMessageHandler.getInstance().handleServerDepositIntoSlotMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerDepositIntoSlotMessage(message);
        }
    }

    public void handleBuyDevelopmentCard(ServerBuyDevelopmentCardMessage message){
        GameView.getInstance().setDevelopmentCardsMarket(message.getView());
        GameView.getInstance().getPlayerFromUser(message.getUser()).setSlots(message.getSlots());
        GameView.getInstance().getCurrentPlayer().setMainAction(false);

        if(gui){
            GUIMessageHandler.getInstance().handleServerBuyDevelopmentCardMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerBuyDevelopmentCardMessage(message);
        }
    }


    public void handleActivateLeaderCardAbility(ServerActivateLeaderCardAbilityMessage message){
        // If production or depot ability have been activated, need to update.
        if (message.getSlots()!= null){
            GameView.getInstance().getPlayerFromUser(message.getUser()).setSlots(message.getSlots());}
        if (message.getWarehouse()!=null) {
            GameView.getInstance().getPlayerFromUser(message.getUser()).setWarehouse(message.getWarehouse());}

        if (!message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            // Other players only know leader cards once activated.
            if (GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards() == null) {
                GameView.getInstance().getPlayerFromUser(message.getUser()).setLeaderCards(new ArrayList<>());
            }
            GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().add(message.getAbility());
        }

        int i = GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().indexOf(message.getAbility());
        GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().get(i).setActive(true);

        if(gui){
            GUIMessageHandler.getInstance().handleServerActivateLeaderCardAbilityMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerActivateLeaderCardAbilityMessage(message);
        }
    }

    public void handleThrowLeaderCard(ServerThrowLeaderCardMessage message){
        if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            // Other players don't know the leader cards

                int index = GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().indexOf(message.getLeaderCard());
                if (index != -1) GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().set(index, null);

        }

        if(gui){
            GUIMessageHandler.getInstance().handleServerThrowLeaderCardAbilityMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerThrowLeaderCardAbilityMessage(message);
        }
    }


    public void handleActivateProduction(ServerActivateProductionMessage message){
        GameView.getInstance().getPlayerFromUser(message.getUser()).setStrongboxView(message.getStrongboxView());
        GameView.getInstance().getCurrentPlayer().setMainAction(false);
        if(gui){
            GUIMessageHandler.getInstance().handleServerActivateProductionMessage(message);

        }else{
            CLIMessageHandler.getInstance().handleServerActivateProductionMessage(message);
        }
    }

    /**
     * This message is received only by the player that disconnects.
     * @param message the disconnection message.
     */
    public void handleDisconnection(DisconnectionMessage message){

        UIController.getInstance().getClientConnection().closeConnection();
        if(gui){
            GUIMessageHandler.getInstance().handleServerDisconnectionMessage();
        }else{
            CLIMessageHandler.getInstance().handleServerDisconnectionMessage();
        }
    }


    public void handleChooseWhiteMarbles(ServerChooseWhiteMarblesMessage message){
        if(gui){
            GUIMessageHandler.getInstance().handleServerChooseWhiteMarblesMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleServerChooseWhiteMarblesMessage(message);
        }
    }


    /**
     * Disconnection message received during the setup phase.
     * Differently from the normal one, this signals that the game is being closed and needs to be restarted.
     * @param message the message object
     */
    public void handleDisconnectionGameSetup(DisconnectionGameSetupMessage message){

        UIController.getInstance().getClientConnection().closeConnection();
        GameView.destroy();


        if(gui){
            GUIMessageHandler.getInstance().handleDisconnectionGameSetupMessage();

        }else {
            CLIMessageHandler.getInstance().handleDisconnectionGameSetupMessage();
        }
    }


    /**
     * Message received when reconnecting to a game. Contains ALL the view information and updates it, setting any parameter that may need to be set.
     * @param message the message containing all the view data.
     */
    public void handleGameReconnection(ServerGameReconnectionMessage message){
        List<User> userList = new ArrayList<>();
        message.getPlayerViews().forEach(playerView -> userList.add(new User(playerView.getNickname())));
        GameView.getInstance(userList);
        GameView.getInstance().setMarketInstance(message.getMarketView());
        GameView.getInstance().setDevelopmentCardsMarket(message.getDevMarketView());
        GameView.getInstance().updatePlayerViews(message.getPlayerViews());
        GameView.getInstance().setCurrentPlayer(message.getCurrentPlayer());
        GameView.getInstance().setBlackCross(message.getBlackCross());

        MatchSettings.getInstance().setTotalUsers(userList.size());

        if (GameView.getInstance().getPlayers().size()==1) MatchSettings.getInstance().setSolo(true);

        if(gui){
            GUIMessageHandler.getInstance().handleServerGameReconnectionMessage(message);
        }else {
            CLIMessageHandler.getInstance().handleServerGameReconnectionMessage(message);
        }

    }

    /**
     * Ends the game by closing the connection when a player as requested to save.
     */
    public void handleGameSaving(){
        UIController.getInstance().getClientConnection().closeConnection();

        if(gui){
            GUIMessageHandler.getInstance().handleGameSaving();
        }else{
            CLIMessageHandler.getInstance().handleGameSaving();
        }
    }


    /**
     * This is the handler for any and all status messages. They are simple messages that do not contain data, and can be received at multiple stages of the game.
     * Since they do not contain information beside their meaning, all the handling is delegated to the UI specific handlers.
     * @param message the status message.
     */
    public void handleStatus(StatusMessage message){
        if(gui){
            GUIMessageHandler.getInstance().handleStatusMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleStatusMessage(message);
        }
    }


    public void handleLastTurns(ServerLastTurnsMessage message) {
        if(gui){
            GUIMessageHandler.getInstance().handleLastTurns(message);
        }else{
            CLIMessageHandler.getInstance().handleLastTurns(message);
        }
    }
}
