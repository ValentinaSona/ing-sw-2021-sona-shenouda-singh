package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.GameActions;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.client.ui.cli.menus.MenuStates;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.ServerGameReconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.util.stream.Collectors;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

/**
 * Singleton class containing the CLI-specific methods for handling the server messages.
 * They are invoked by the DispatcherController after the UI-agnostic handling of the messages.
 */

public class CLIMessageHandler {

    private static CLIMessageHandler singleton;
    private final CLI cli;

    public CLIMessageHandler(CLI cli) {
        this.cli = cli;
    }

    public static void getInstance(CLI cli) {
        if (singleton == null) {
            singleton = new CLIMessageHandler(cli);
        }
    }

    public static CLIMessageHandler getInstance() {
        return singleton;
    }


    /**
     * Handles status messages. Directly handles unique ones, invokes apposite functions for other ones.
     * @param msg status message to be handled.
     */
    public void handleStatusMessage(StatusMessage msg) {
        switch (msg) {
            case OK_NICK -> MenuRunner.getInstance().sendResponse(GameActions.MENU, "[" + CHECK_MARK + "] The server has received your nickname ");

            case SET_COUNT -> {
                if (MenuRunner.getInstance().getState() ==MenuStates.RESUME){
                    UIController.getInstance().loadGameFromFile();
                }
                else if (!MenuRunner.getInstance().isSolo()) {
                    cli.printMessage("[ ] You are the first player! Select the size of the lobby (2-4 players): ");
                    UIController.getInstance().setCreation(cli.getInt(2, 4));
                }
            }
            case OK_COUNT -> {
                if (MenuRunner.getInstance().getState() ==MenuStates.RESUME){
                    cli.printMessage("[" + CHECK_MARK + "] The server has restored the game. Wait for the other players to rejoin! ");
                }else if (!MenuRunner.getInstance().isSolo()) {
                    cli.printMessage("[" + CHECK_MARK + "] The server has created the lobby! Wait for other players to join! ");
                }
            }

            case JOIN_LOBBY -> {

                if (MenuRunner.getInstance().getState() == MenuStates.RESUME){
                    cli.printMessage("[" + CHECK_MARK + "] The server has restored the game. Wait for the other players to rejoin! ");
                } else {cli.printMessage("[" + CHECK_MARK + "] Found an already existing lobby to join.");}
            }

            case CLIENT_ERROR -> handleClientError();

            case REQUIREMENTS_ERROR -> handleRequirementsError();

            case SELECTION_ERROR -> handleSelectionError();

            case EMPTY_ERROR -> {
                if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
                    MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.MENU, "[X] There are no cards in this deck.");
            }

            case CONTINUE -> handleContinue();

            case RECONNECTION_OK ->  MenuRunner.getInstance().sendResponse(GameActions.MENU, "[" + CHECK_MARK + "] Rejoining the game!");

            default -> cli.printMessage("Server Says: " + msg);
        }


    }

    /**
     * Various situations in which the SELECTION_ERROR status message incurs.
     */
    private void handleSelectionError() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.MENU, "[X] This card cannot be placed on top of this slot.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.BUY_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.BUY_CARD, GameActions.MENU, "[X] The resources selected are not sufficient to buy this card.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_PRODUCTION)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_PRODUCTION, GameActions.MENU, "[X] The resources selected are not sufficient to activate this production.");
    }
    /**
     * Various situations in which the CONTINUE status message incurs.
     */
    private void handleContinue() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.TIDY_WAREHOUSE)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE, "[X] These depots are both empty! Did you pick the right ones?");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.BUY_CARD, "[" + CHECK_MARK + "] Select the resources needed to pay.");

    }
    /**
     * Various situations in which the REQUIREMENTS_ERROR status message incurs.
     */
    private void handleRequirementsError() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.TIDY_WAREHOUSE)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE, "[X] These two depots cannot be swapped.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.ACTIVATE_LEADER)
            MenuRunner.getInstance().sendResponse(GameActions.ACTIVATE_LEADER, "[X] You do not meet the requirements to activate this card.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.THROW_LEADER)
            MenuRunner.getInstance().sendResponse(GameActions.THROW_LEADER, "[X] Cannot throw away an active leader card.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.MENU, "[X] You do not have the resources needed to buy this card.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.BUY_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.BUY_CARD, GameActions.MENU, "[X] You have tried to subtract from a depot more resources than it contains.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.DEPOSIT_RESOURCES)
            MenuRunner.getInstance().sendResponse(GameActions.DEPOSIT_RESOURCES, "[X] This resource cannot be deposited there.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_PRODUCTION)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_PRODUCTION, GameActions.MENU, "[X] The sources selected do not contain the resources selected.");


    }

    /**
     * Various situations in which the CLIENT_ERROR status message incurs.
     */
    private void handleClientError() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.TIDY_WAREHOUSE || MenuRunner.getInstance().getContextAction() == GameActions.BUY_MARBLES)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.ACTIVATE_LEADER)
            MenuRunner.getInstance().sendResponse(GameActions.ACTIVATE_LEADER, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.BUY_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.BUY_CARD, GameActions.MENU, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.MENU, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.ACQUIRE_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.ACQUIRE_CARD, GameActions.MENU, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_PRODUCTION)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_PRODUCTION, GameActions.MENU, "[X] This operation is unavailable right now.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.DEPOSIT_RESOURCES)
            MenuRunner.getInstance().sendResponse(GameActions.DEPOSIT_RESOURCES, "[X] This resource is not amongst the obtained ones.");
        else if (MenuRunner.getInstance().getState() == MenuStates.MAIN)
            MenuRunner.getInstance().sendResponse(GameActions.MENU, GameActions.END_TURN, "[X] This nickname is already taken. Try to join with a different one.");
        else  if (MenuRunner.getInstance().getContextAction() == GameActions.END_TURN)
            MenuRunner.getInstance().sendResponse(GameActions.END_TURN, "[X] You cannot end your turn without buying from a market or activating your productions");

    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {

        cli.printMessage("[" + CHECK_MARK + "] Another player has joined! (" + message.getLobbyUsers().size() + "/" + message.getNumOfPlayer() + ")");

    }

    public void handleServerSetupGameMessage(ServerSetupGameMessage message) {

        String players = message.getUsers().stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "", ""));
        if (!MenuRunner.getInstance().isSolo()) {
            cli.printMessage("[" + CHECK_MARK + "] The lobby is full! The game is starting!");
            cli.printMessage("[" + CHECK_MARK + "] Players:" + players);
        } else {
            cli.printMessage("[" + CHECK_MARK + "] The game is starting! You're up against Lorenzo il Magnifico!");
        }

        // Saves the user's playerView for ease of access.
        cli.setView();

    }

    /**
     * This message is received by ALL users. Only the user indicated in the message proceeds to handle its contents.
     * Other users are notified that another player is choosing their cards.
     * It's an interrupting method that refreshes a player's menu options (see CLI.getChoice()).
     * It also wakes up the MenuRunner so that it enters the setup state.
     * @param message contains resource number, 4 leader cards, a faith track and the addressed user.
     */
    public void handleServerSetupUserMessage(ServerSetupUserMessage message) {

        MenuRunner.getInstance().setState(MenuStates.SETUP, message);

        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            cli.printMessage("[" + CHECK_MARK + "] It's your turn!");

            synchronized (CLIMessageHandler.getInstance()) {
                cli.setInterrupted(true);
                CLIMessageHandler.getInstance().notifyAll();
            }

            if (message.getFaithTrackView() != null && message.getFaithTrackView().getFaithMarker() > 0)
                cli.printMessage("[" + CHECK_MARK + "] You have received " + message.getFaithTrackView().getFaithMarker() + " faith points.");
        } else {
            cli.printMessage("[ ] " + message.getUser().getNickName() + " is selecting their leader cards.");
        }

        synchronized (MenuRunner.getInstance()) {
            MenuRunner.getInstance().notifyAll();
        }

    }

    public void handleServerSetupActionMessage(ServerSetupActionMessage message) {

        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            cli.printMessage("[" + CHECK_MARK + "] You have received the selected cards and resources");


        } else {
            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has selected their leader cards.");
        }

    }

    /**
     * Received by all users at the start of someone's turn
     * It's an interrupting method that refreshes a player's menu options (see CLI.getChoice()).
     * It also wakes up the MenuRunner so that it enters the GAME state. (If it's not in the REJOIN state, in which case it is already awake).
     * @param message handled message.
     */
    public void handleServerStartTurnMessage(ServerStartTurnMessage message) {
        // If it's the first turn.
        if (MenuRunner.getInstance().getState() == MenuStates.SETUP)
            cli.printMessage("[" + CHECK_MARK + "] The game is starting!");

        if (MenuRunner.getInstance().getState() != MenuStates.REJOIN) MenuRunner.getInstance().setState(MenuStates.GAME, message);
        if (message.getStartingTurn().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            cli.printMessage("[" + CHECK_MARK + "] It's your turn!");

            synchronized (CLIMessageHandler.getInstance()) {
                cli.setInterrupted(true);
                CLIMessageHandler.getInstance().notifyAll();
            }

        } else if (message.getEndingTurn().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            MenuRunner.getInstance().sendResponse(GameActions.END_TURN, "[" + CHECK_MARK + "] Your turn has ended. Meanwhile you can observe the board or rearrange your warehouse.");
        } else
            cli.printMessage("[ ] It's " + message.getStartingTurn().getNickName() + "'s turn!");

        synchronized (MenuRunner.getInstance()) {
            MenuRunner.getInstance().notifyAll();
        }


    }

    public void handleServerWarehouseMessage(ServerWarehouseMessage message) {

        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE, "[" + CHECK_MARK + "] The depots contents have been swapped.");

    }

    public void handleServerBoughtMarblesMessage(ServerBoughtMarblesMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.BUY_MARBLES, GameActions.DEPOSIT_RESOURCES, "[" + CHECK_MARK + "] You have acquired resources from the market. You can now proceed to deposit them.");
        else
            cli.printMessage("[ ] " + message.getUser().getNickName() + " is buying resources from the market.");

    }

    public void handleServerChooseWhiteMarblesMessage(ServerChooseWhiteMarblesMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            MenuRunner.getInstance().setInMsg(message);
            MenuRunner.getInstance().sendResponse(GameActions.BUY_MARBLES, GameActions.TWO_LEADERS, "[" + CHECK_MARK + "] You have two white marble abilities active and need to choose which resources to obtain.");
        }
    }

    public void handleServerFaithTrackMessage(ServerFaithTrackMessage message) {

        if(message.getUser()==null){
            if (message.isReport()) {
                cli.printMessage("[!] Lorenzo has received " + message.getFaith()+ " faith points and triggered a vatican report. Check the faith tracks to see the results!");
            } else
                cli.printMessage("[!] Lorenzo has received " + message.getFaith()+ " faith points.");

            return;
        }

        if (message.isReport()) {
            if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("[!] A vatican report has been triggered. Check the faith tracks to see the results!");
            else
                if (message.getFaith()!= 0)
                    cli.printMessage("[!] " + message.getUser().getNickName() + " has received " + message.getFaith() + " faith points and triggered a vatican report! Check your faith track!");
        } else {
            if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("[" + CHECK_MARK + "] You have received " + message.getFaith() + " faith points.");
            else
                cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has received " + message.getFaith() + " faith points.");
        }
    }

    public void handleServerDepositActionMessage(ServerDepositActionMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.DEPOSIT_RESOURCES, "[" + CHECK_MARK + "] The resources have been deposited.");
        else
            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has acquired " + message.getBought() + ".");
    }

    public void handleServerThrowResourceMessage(ServerThrowResourceMessage message) {

        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            cli.printMessage("[" + CHECK_MARK + "] You have thrown " + message.getThrownResources() + " resources. Other player will receive an equal number of faith points.");
        else
            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has thrown away " + message.getThrownResources() + " resources.");
    }

    public void handleServerActivateLeaderCardAbilityMessage(ServerActivateLeaderCardAbilityMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.ACTIVATE_LEADER, "[" + CHECK_MARK + "] You have activated the selected leader card. Its special ability is now at your disposal. ");
        else
            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has activated a leader card! Check their board to see its ability.");
    }

    public void handleServerThrowLeaderCardAbilityMessage(ServerThrowLeaderCardMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.THROW_LEADER, "[" + CHECK_MARK + "] You have thrown away the selected leader card.");
        else
            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has thrown away a leader card.");

    }

    public void handleServerDepositIntoSlotMessage(ServerDepositIntoSlotMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            if (MenuRunner.getInstance().getContextAction() == GameActions.BUY_CARD)
                MenuRunner.getInstance().sendResponse(GameActions.BUY_CARD, "[" + CHECK_MARK + "] Your resources have been spent to buy the card.");
            else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_PRODUCTION)
                MenuRunner.getInstance().sendResponse(GameActions.SELECT_PRODUCTION, "[" + CHECK_MARK + "] Your resources have been spent to pay the production cost.");
    }

    public void handleServerBuyDevelopmentCardMessage(ServerBuyDevelopmentCardMessage message) {
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
            MenuRunner.getInstance().sendResponse(GameActions.ACQUIRE_CARD, "[" + CHECK_MARK + "] You have acquired the following card:\n" + message.getCard());
        else
            MenuRunner.getInstance().sendResponse(GameActions.ACQUIRE_CARD, "[" + CHECK_MARK + "] "+ message.getUser().getNickName() +" has acquired the following card:\n" + message.getCard());

    }

    public void handleServerActivateProductionMessage(ServerActivateProductionMessage message) {
        String costPrint = message.getSpent().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "", ""));
        String gainPrint = message.getGained().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "", ""));
        if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {
            MenuRunner.getInstance().sendResponse(GameActions.ACTIVATE_PRODUCTION, "[" + CHECK_MARK + "] You have spent " + costPrint + " and received " + gainPrint + ".");

        } else {

            cli.printMessage("[" + CHECK_MARK + "] " + message.getUser().getNickName() + " has spent " + costPrint + " to activate their productions and received " + gainPrint + ".");
        }
    }

    public void handleServerServerSoloDiscardMessage(ServerSoloDiscardMessage message) {
        cli.printMessage("[!] Lorenzo has discarded two "+message.getType()+" development cards from the market!");
    }

    public void handleServerSoloMoveMessage(ServerSoloMoveMessage message) {
        if (message.isShuffled())
            cli.printMessage("[!] Lorenzo has gained 1 faith points and has shuffled his actions.");
        else cli.printMessage("[!] Lorenzo has gained 2 faith points");
    }

    public void handleServerFinalScoreMessage(ServerFinalScoreMessage message) {
        MenuRunner.getInstance().setState(MenuStates.END);



        cli.printMessage("[!] Here is the final score!");
        message.getRank().forEach((u,s) -> cli.printMessage(u.getNickName() + " : "+ s));
        // Win message

        MenuRunner.getInstance().sendResponse(GameActions.MENU, "[X] You will be returned to main menu.");

        synchronized (CLIMessageHandler.getInstance()) {
            cli.setInterrupted(true);
            CLIMessageHandler.getInstance().notifyAll();
        }
        // Force end of turn method?

    }

    public void handleServerDisconnectionMessage() {
        if (MenuRunner.getInstance().getState() == MenuStates.MAIN)
            // Tried to rejoin a game, no game or no player by that nickname found.
            MenuRunner.getInstance().sendResponse(GameActions.MENU, "[X] No game found.");
        else {

            MenuRunner.getInstance().setState(MenuStates.END);
            MenuRunner.getInstance().sendResponse(GameActions.MENU, "[X] You have been disconnected. The client will be closed.");

            synchronized (CLIMessageHandler.getInstance()) {
                cli.setInterrupted(true);
                CLIMessageHandler.getInstance().notifyAll();
            }
            System.exit(0);
        }
    }

    public void handleServerGameReconnectionMessage(ServerGameReconnectionMessage message) {

        // Saves the user's playerView for ease of access.
        cli.setView();

        MenuRunner.getInstance().getGameMenu().setRunner(MenuRunner.getInstance());

        cli.printMessage("[" + CHECK_MARK + "] Rejoining the game. Check what you have missed!");


        if (message.isPendingAction()) {

            MenuRunner.getInstance().setState(MenuStates.REJOIN);

        } else {

            MenuRunner.getInstance().setState(MenuStates.GAME);

        }
        synchronized (MenuRunner.getInstance()) {
            MenuRunner.getInstance().notifyAll();
        }
    }

    public void handleDisconnectionGameSetupMessage() {
        if (MenuRunner.getInstance().getState() == MenuStates.SAVING)
            cli.printMessage("["+ CHECK_MARK+ "] The game has been saved. Returning you to main menu - you may resume it whenever you wish.");
        cli.printMessage("[X] A player has disconnected. Returning you to main menu - try to start a new game!");
        MenuRunner.getInstance().setState(MenuStates.END);

        synchronized (CLIMessageHandler.getInstance()) {
            cli.setInterrupted(true);
            CLIMessageHandler.getInstance().notifyAll();
        }
    }

    public void handleGameSaving() {

        cli.printMessage("[!] The game has been stopped by a player. To continue it, all of the current players must resume playing with the same nickname.");

        MenuRunner.getInstance().setState(MenuStates.SAVING);
    }

    public void handleLastTurns(ServerLastTurnsMessage message) {

        switch (message.getCause()){
            case FAITH_END -> cli.printMessage("\n[!] A player has triggered the 3rd Vatican Report!");
            case SEVENTH_CARD -> cli.printMessage("\n[!] A player has acquired 7 development cards!");
            case LORENZO_FAITH -> cli.printMessage("\n[!] Lorenzo has reached the end of his faith track. You lost!");
            case LORENZO_DISCARD -> cli.printMessage("\n[!] Lorenzo has discarded all the cards of one color. You lost!");
            case DEBUG -> cli.printMessage("\n[!] A player has ended the game with a secret debug code!");
        }

        if (message.getLastUsers() == null) return;

        String players = message.getLastUsers().stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "", ""));

        cli.printMessage("[ ] These users will now play their last turn: " + players);
    }
}