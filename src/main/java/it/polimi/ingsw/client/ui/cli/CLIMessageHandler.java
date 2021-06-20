package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.utils.GameActions;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.client.ui.cli.menus.MenuStates;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.util.stream.Collectors;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

public class CLIMessageHandler {

    private static CLIMessageHandler singleton;
    private final CLI cli;
    private final MenuRunner menu;

    public CLIMessageHandler(CLI cli) {
        this.cli = cli;
        this.menu = MenuRunner.getInstance();
    }

    public static CLIMessageHandler getInstance(CLI cli) {
        if (singleton == null) {
            singleton = new CLIMessageHandler(cli);
        }
        return singleton;
    }

    public static CLIMessageHandler getInstance() {
        return singleton;
    }


    /**
     * Handles status messages. Directly handles unique ones, invokes apposite functions for other ones.
     *
     * @param msg status message to be handled.
     */
    public void handleStatusMessage(StatusMessage msg) {
        switch (msg) {
            case OK_NICK -> MenuRunner.getInstance().sendResponse(GameActions.MENU, "[" + CHECK_MARK + "] The server has received your nickname ");

            case SET_COUNT -> {
                if (!MenuRunner.getInstance().isSolo()) {
                    cli.printMessage("[ ] You are the first player! Select the size of the lobby (2-4 players): ");
                    UIController.getInstance().setCreation(cli.getInt(2, 4));
                }
            }
            case OK_COUNT -> {
                if (!MenuRunner.getInstance().isSolo()) {
                    cli.printMessage("[" + CHECK_MARK + "] The server has created the lobby! Wait for other players to join! ");
                }
            }
            case JOIN_LOBBY -> cli.printMessage("[" + CHECK_MARK + "] Found an already existing lobby to join.");

            case CLIENT_ERROR -> handleClientError();

            case REQUIREMENTS_ERROR -> handleRequirementsError();

            case SELECTION_ERROR -> handleSelectionError();

            case CONTINUE -> handleContinue();

            default -> cli.printMessage("Server Says: " + msg);
        }


    }

    private void handleSelectionError() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.MENU, "[X] This card cannot be placed on top of this slot.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.BUY_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.BUY_CARD, GameActions.MENU, "[X] The resources selected are not sufficient to buy this card.");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_PRODUCTION)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_PRODUCTION, GameActions.MENU, "[X] The resources selected are not sufficient to activate this production.");
    }

    private void handleContinue() {
        if (MenuRunner.getInstance().getContextAction() == GameActions.TIDY_WAREHOUSE)
            MenuRunner.getInstance().sendResponse(GameActions.TIDY_WAREHOUSE, "[X] These depots are both empty! Did you pick the right ones?");
        else if (MenuRunner.getInstance().getContextAction() == GameActions.SELECT_CARD)
            MenuRunner.getInstance().sendResponse(GameActions.SELECT_CARD, GameActions.BUY_CARD, "[" + CHECK_MARK + "] Select the resources needed to pay.");

    }

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
    }

    public void handleUpdateLobbyMessage(ServerUpdateLobbyMessage message) {

        cli.printMessage("[" + CHECK_MARK + "] Another player has joined! (" + message.getLobbyUsers().size() + "/" + message.getNumOfPlayer() + ")");

    }

    public void handleServerSetupGameMessage(ServerSetupGameMessage message) {

        if (!MenuRunner.getInstance().isSolo()) {
            cli.printMessage("[" + CHECK_MARK + "] The lobby is full! The game is starting!");
        } else {
            cli.printMessage("[" + CHECK_MARK + "] The game is starting! You're up against Lorenzo il Magnifico!");
        }
        // Saves the user's playerView for ease of access.
        cli.setView();

    }

    /**
     * This message is received by ALL users. Only the user indicated in the message proceeds to handle its contents.
     * Other users are notified that another player is choosing their cards.
     *
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

    public void handleServerStartTurnMessage(ServerStartTurnMessage message) {
        // If it's the first turn.
        if (MenuRunner.getInstance().getState() == MenuStates.SETUP)
            cli.printMessage("[" + CHECK_MARK + "] The game is starting!");

        MenuRunner.getInstance().setState(MenuStates.GAME, message);
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
                cli.printMessage("[!] Lorenzo has received " + message.getFaith()+ "faith points and triggered a vatican report. Check the faith tracks to see the results!");
            } else
                cli.printMessage("[!] Lorenzo has received " + message.getFaith()+ "faith points.");

            return;
        }

        if (message.isReport()) {
            if (message.getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("[" + CHECK_MARK + "] You have received " + message.getFaith() + " faith points and triggered a vatican report. Check the faith tracks to see the results!");
            else
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
            MenuRunner.getInstance().sendResponse(GameActions.ACQUIRE_CARD, "[" + CHECK_MARK + "] You have acquired the following card: ");
    }

    public void handleServerActivateProductionMessage(ServerActivateProductionMessage message) {
        String costPrint = message.getSpent().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" , ", "", ""));
        String gainPrint = message.getGained().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" , ", "", ""));
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

        cli.printMessage("[!] The game has ended! Here is the final score!");
        message.getRank().forEach((u,s) ->{ cli.printMessage(u.getNickName() + " : "+ s); });
        // Win message

        MenuRunner.getInstance().sendResponse(GameActions.MENU, "[X] The game has ended. You will be returned to main menu.");

        synchronized (CLIMessageHandler.getInstance()) {
            cli.setInterrupted(true);
            CLIMessageHandler.getInstance().notifyAll();
        }
        // Force end of turn method?

    }
}