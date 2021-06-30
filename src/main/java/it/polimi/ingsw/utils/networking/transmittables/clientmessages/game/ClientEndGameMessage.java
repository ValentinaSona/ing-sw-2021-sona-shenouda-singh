package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameCause;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerLastTurnsMessage;

import java.util.ArrayList;

/**
 * Message created for testing purpose - automatically triggers end of game if called.
 */
public class ClientEndGameMessage implements ClientMessage, ControllerHandleable {


    public ClientEndGameMessage(){
    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user) {
        Game model = Game.getInstance();
        Player currentPlayer = model.getCurrentPlayer();

        var players = model.getPlayers();
        handler.turnController.setEndGame(true);

        // If the game is single player or the player is the last in the turn order, the game ends.
        if (model.isSolo() || players.lastIndexOf(currentPlayer) == ( players.size() - 1 ) ){

            model.notify(new ServerLastTurnsMessage(null, EndOfGameCause.DEBUG));
            handler.turnController.endOfGame();

            return true;
        }

        var lastUsers = new ArrayList<User>();

        for (int i = players.lastIndexOf(currentPlayer); i < players.size(); i++){
            lastUsers.add(model.getUserFromPlayer( players.get(i)) );
        }

        model.notify(new ServerLastTurnsMessage(lastUsers, EndOfGameCause.DEBUG));

        return  true;
    }

}