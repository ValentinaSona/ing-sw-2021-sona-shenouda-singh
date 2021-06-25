package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.cli.CLIMessageHandler;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import it.polimi.ingsw.client.ui.gui.JFXControllers.ScreenName;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
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

public class DispatcherController implements Runnable, LambdaObserver {
    private static DispatcherController singleton;
    private UiControllerInterface currentController;
    private final BlockingQueue<Transmittable> serverMessages= new LinkedBlockingDeque<>();
    private final boolean gui;

    public static DispatcherController getInstance(boolean gui){
        if(singleton == null){
            singleton = new DispatcherController(gui);
        }

        return singleton;
    }

    //method to use only when the class has already been created
    public static DispatcherController getInstance() {
        return singleton;
    }

    private DispatcherController(boolean gui){
        this.gui = gui;
    }

    public void setCurrentController(UiControllerInterface currentController) {
        this.currentController = currentController;
    }

    public UiControllerInterface getCurrentController() {
        return currentController;
    }

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
                    ClientHandleable handleable = (ClientHandleable) message;
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

        }else{
            CLIMessageHandler.getInstance().handleServerServerSoloDiscardMessage(message);
        }
    }

    public void handleSoloMove(ServerSoloMoveMessage message){
        GameView.getInstance().setBlackCross(message.getBlackCross());

        if(gui){

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
            GameView.getInstance().setBlackCross(message.getFaith());
        else GameView.getInstance().getPlayerFromUser(message.getUser()).setFaithTrackView(message.getFaithTrackView());

        if(gui){

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

        if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            // Other players don't know the leader cards in a player's hand.
            int i = GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().indexOf(message.getAbility());
            GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().get(i).setActive(true);
        } else {
            // But they know them once activated.
            if (GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards() == null) {
                GameView.getInstance().getPlayerFromUser(message.getUser()).setLeaderCards(new ArrayList<LeaderCard>());
            }
            GameView.getInstance().getPlayerFromUser(message.getUser()).getLeaderCards().add(message.getAbility());
        }


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
        if(message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            GameView.getInstance().getPlayerFromUser(message.getUser()).setStrongboxView(message.getStrongboxView());

        GameView.getInstance().getCurrentPlayer().setMainAction(false);
        if(gui){
            GUIMessageHandler.getInstance().handleServerActivateProductionMessage(message);

        }else{
            CLIMessageHandler.getInstance().handleServerActivateProductionMessage(message);
        }
    }

    public void handleDisconnection(DisconnectionMessage message){
        //METODO solo per partita in multiplayer
        UIController.getInstance().getClientConnection().closeConnection();
        if(gui){
            //decido cosa fare una volta disconnesso
        }else{
            CLIMessageHandler.getInstance().handleServerDisconnectionMessage();
        }
    }


    public void handleChooseWhiteMarbles(ServerChooseWhiteMarblesMessage message){
        if(gui){

        }else{
            CLIMessageHandler.getInstance().handleServerChooseWhiteMarblesMessage(message);
        }
    }

    // messaaggio di disconnessione
    // durante la fase di setup viene chiusa la partita e bisogna riniziarne un altra
    public void handleDisconnectionGameSetup(DisconnectionGameSetupMessage message){

        UIController.getInstance().getClientConnection().closeConnection();
        GameView.destroy();


        if(gui){

        }else {
            CLIMessageHandler.getInstance().handleDisconnectionGameSetupMessage();
        }
    }


    // messaggio che ricevo dopo che mi riconnetto ad una partita contiene info sulla partita e i giocatori
    public void handleGameReconnection(ServerGameReconnectionMessage message){
        List<User> userList = new ArrayList<>();
        message.getPlayerViews().forEach(playerView -> userList.add(new User(playerView.getNickname())));
        GameView.getInstance(userList);
        GameView.getInstance().setMarketInstance(message.getMarketView());
        GameView.getInstance().setDevelopmentCardsMarket(message.getDevMarketView());
        GameView.getInstance().updatePlayerViews(message.getPlayerViews());

        if(gui){

        }else {
            CLIMessageHandler.getInstance().handleServerGameReconnectionMessage(message);
        }

    }

    //TODO termian la partita perche un giocatore ha richiesto il salvataggio
    public void handleGameSaving(){
        UIController.getInstance().getClientConnection().closeConnection();

        if(gui){

        }else{
            CLIMessageHandler.getInstance().handleGameSaving();
        }
    }


    //TODO addesso se nella fase di join della lobby mi viene mandato il Set_count posso decidere se caricare una partita da file
    //mandando il messaggio GameLoadFromFile
    public void handleStatus(StatusMessage message){
        if(gui){
            GUIMessageHandler.getInstance().handleStatusMessage(message);
        }else{
            CLIMessageHandler.getInstance().handleStatusMessage(message);
        }
    }
}
