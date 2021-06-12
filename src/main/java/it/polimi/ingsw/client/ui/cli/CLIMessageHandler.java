package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.menus.GameActions;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.client.ui.cli.menus.MenuStates;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

public class CLIMessageHandler {

    private static CLIMessageHandler singleton;
    private final CLI cli;
    private final MenuRunner menu;

    public CLIMessageHandler(CLI cli) {
        this.cli = cli;
        this.menu = MenuRunner.getInstance();
    }

    public static CLIMessageHandler getInstance(CLI cli){
        if(singleton == null){
            singleton = new CLIMessageHandler(cli);
        }
        return singleton;
    }
    public static CLIMessageHandler getInstance(){
        return singleton;
    }


    /**
     * Handles status messages. Directly handles unique ones, invokes apposite functions for other ones.
     * @param msg status message to be handled.
     */
    public void handleStatusMessage(StatusMessage msg){
        switch (msg){
            case OK_NICK ->  cli.printMessage("["+CHECK_MARK+"] The server has received your nickname ");

            case SET_COUNT -> {
                cli.printMessage("[ ] You are the first player! Select the size of the lobby (2-4 players): ");
                UIController.getInstance().setCreation( cli.getInt(2,4));
            }
            case OK_COUNT -> cli.printMessage("["+CHECK_MARK+"] The server has created the lobby! Wait for other players to join! ");

            case JOIN_LOBBY -> cli.printMessage("["+CHECK_MARK+"] Found an already existing lobby to join.");

            case CLIENT_ERROR -> handleClientError();

            case REQUIREMENTS_ERROR -> handleRequirementsError();

            case CONTINUE -> handleContinue();

            default ->  cli.printMessage("Server Says: "+ msg);
        }


    }

    private void handleContinue() {
        if(MenuRunner.getInstance().getContextAction()==GameActions.TIDY_WAREHOUSE)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE,"[X] These depots are both empty! Did you pick the right ones?");


    }

    private void handleRequirementsError() {
        if(MenuRunner.getInstance().getContextAction()==GameActions.TIDY_WAREHOUSE)
           MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE,"[X] These two depots cannot be swapped.");

    }

    private void handleClientError() {
        if(MenuRunner.getInstance().getContextAction()==GameActions.TIDY_WAREHOUSE || MenuRunner.getInstance().getContextAction()==GameActions.BUY_MARBLES)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE,"[X] This operation is unavailable right now.");

    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message){

        cli.printMessage("["+CHECK_MARK+"] Another player has joined! ("+message.getLobbyUsers().size()+"/"+message.getNumOfPlayer()+")");

    }

    public void handleServerSetupGameMessage(ServerSetupGameMessage message){

        cli.printMessage("["+CHECK_MARK+"] The lobby is full! The game is starting!");

        // Saves the user's playerView for ease of access.
        cli.setView();

    }

    /**
     * This message is received by ALL users. Only the user indicated in the message proceeds to handle its contents.
     * Other users are notified that another player is choosing their cards.
     * @param message contains resource number, 4 leader cards, a faith track and the addressed user.
     */
    public void handleServerSetupUserMessage(ServerSetupUserMessage message){

        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() )) {
            cli.printMessage("["+CHECK_MARK+"] It's your turn! If you don't see new options in the menu, pick any one option to refresh it.");

            synchronized (CLIMessageHandler.getInstance()) {
                cli.setInterrupted(true);
                CLIMessageHandler.getInstance().notifyAll();
            }

            if (message.getFaithTrackView() != null && message.getFaithTrackView().getFaithMarker() > 0)
                cli.printMessage("["+CHECK_MARK+"] You have received " + message.getFaithTrackView().getFaithMarker() + " faith points.");
        } else {
            cli.printMessage("[ ] " + message.getUser().getNickName() + " is selecting their leader cards.");
        }



        MenuRunner.getInstance().setState(MenuStates.SETUP, message);
        synchronized (MenuRunner.getInstance()) {
            MenuRunner.getInstance().notifyAll();
        }

        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() )) {
            synchronized (CLIMessageHandler.getInstance()) {
                cli.setInterrupted(true);
                CLIMessageHandler.getInstance().notifyAll();
            }
        }
    }

    public void handleServerSetupActionMessage(ServerSetupActionMessage message){

        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() )) {
            cli.printMessage("["+CHECK_MARK+"] You have received the selected cards and resources");


        } else {
            cli.printMessage("["+CHECK_MARK+"] " + message.getUser().getNickName() + " has selected their leader cards.");
        }

    }

    public void handleServerStartTurnMessage(ServerStartTurnMessage message){
        // If it's the first turn.
        if (MenuRunner.getInstance().getState() == MenuStates.SETUP)
            cli.printMessage("["+CHECK_MARK+"] The game is starting!");

        MenuRunner.getInstance().setState(MenuStates.GAME, message);
        if ( message.getStartingTurn().getNickName().equals( MatchSettings.getInstance().getClientNickname() )) {
            cli.printMessage("[" + CHECK_MARK + "] It's your turn!");

            synchronized (System.in) {
                cli.setInterrupted(true);
                System.in.notifyAll();
            }
        } else
            cli.printMessage("[ ] It's" + message.getStartingTurn().getNickName() + "'s turn! Meanwhile you can observe the board or rearrange your warehouse.");

        synchronized (MenuRunner.getInstance()) {
            MenuRunner.getInstance().notifyAll();
        }

    }


    public void handleServerWarehouseMessage(ServerWarehouseMessage message){

        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() ))
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE,"[" + CHECK_MARK + "] The depots contents have been swapped.");

    }

    public void handleServerBoughtMarblesMessage(ServerBoughtMarblesMessage message) {
        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() ))
            MenuRunner.getInstance().sendResponse(GameActions.BUY_MARBLES, GameActions.DEPOSIT_RESOURCES, "[" + CHECK_MARK + "] You have acquired resources from the market. You can now proceed to deposit them.");
    }

    public void handleServerFaithTrackMessage(ServerFaithTrackMessage message) {
        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() ))
            cli.printMessage( "[" + CHECK_MARK + "] You have received "+ message.getFaith()+ " faith points.");

    }

    public void handleServerDepositActionMessage(ServerDepositActionMessage message) {
        if ( message.getUser().getNickName().equals( MatchSettings.getInstance().getClientNickname() ))
            MenuRunner.getInstance().sendResponse(GameActions.DEPOSIT_RESOURCES,"[" + CHECK_MARK + "] The resources have been deposited.");
    }
}
