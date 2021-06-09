package it.polimi.ingsw.client.ui.cli.controllers;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

public class CLIMessageHandler {

    private static CLIMessageHandler singleton;
    private final CLI cli;
    private final Menu menu;

    public CLIMessageHandler(CLI cli) {
        this.cli = cli;
        this.menu = Menu.getInstance();
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

    public void handleStatusMessage(StatusMessage msg){
        switch (msg){
            case OK_NICK ->  cli.printMessage("["+CHECK_MARK+"] The server has received your nickname ");
            case SET_COUNT -> {
                cli.printMessage("[ ] You are the first player! Select the size of the lobby (2-4 players): ");
                UIController.getInstance().setCreation( cli.getInt(2,4));
            }
            case OK_COUNT -> cli.printMessage("["+CHECK_MARK+"] The server has created the lobby! Wait for other players to join! ");
            default ->  cli.printMessage("Server Says: "+ msg);
        }


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

            if (message.getFaithTrackView() != null && message.getFaithTrackView().getFaithMarker() > 0)
                cli.printMessage("["+CHECK_MARK+"] You have received " + message.getFaithTrackView().getFaithMarker() + " faith points.");
        } else {
            cli.printMessage("[ ] " + message.getUser().getNickName() + " is selecting their leader cards. You can explore the board in the meantime.");
        }



        Menu.getInstance().setState(MenuStates.SETUP, message);
        synchronized (Menu.getInstance()) {
            Menu.getInstance().notifyAll();
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

    }




}
