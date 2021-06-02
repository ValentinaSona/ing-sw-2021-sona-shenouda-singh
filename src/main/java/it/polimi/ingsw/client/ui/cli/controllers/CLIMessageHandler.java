package it.polimi.ingsw.client.ui.cli.controllers;

import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;

public class CLIMessageHandler {

    private static CLIMessageHandler singleton;
    private final CLI cli;

    public CLIMessageHandler(CLI cli) {
        this.cli = cli;
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
        Menu.getInstance().setupMenu();

    }

    public void handleServerSetupUserMessage(ServerSetupUserMessage message){

        cli.printMessage("["+CHECK_MARK+"] It's your turn! Select \"Pick starting resources\" to continue.");
        Menu.getInstance().setSetupTurn(true);

    }
}
