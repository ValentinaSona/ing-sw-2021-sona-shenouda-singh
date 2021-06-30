package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerLastTurnsMessage;

import java.util.ArrayList;

public class ClientThrowResourcesMessage implements ClientMessage, ControllerHandleable {


    public  ClientThrowResourcesMessage(){

    }

    @Override
    public boolean handleMessage(Controller handler, RemoteViewHandler view, User user){
        try {
            handler.resourceController.throwResources( view, user);
        } catch (EndOfGameException e) {
            endOfGame(handler, e);
        }
        return  true;
    }
    private void endOfGame(Controller handler, EndOfGameException e) {
        Game model = Game.getInstance();
        Player currentPlayer = model.getCurrentPlayer();

        var players = model.getPlayers();
        handler.turnController.setEndGame(true);

        // If the game is single player or the player is the last in the turn order, the game ends.
        if (model.isSolo() || players.lastIndexOf(currentPlayer) == ( players.size() - 1 ) ){

            model.notify(new ServerLastTurnsMessage(null, e.getEndCause()));
            handler.turnController.endOfGame();

            return;
        }

        var lastUsers = new ArrayList<User>();

        for (int i = players.lastIndexOf(currentPlayer); i < players.size(); i++){
            lastUsers.add(model.getUserFromPlayer( players.get(i)) );
        }

        model.notify(new ServerLastTurnsMessage(lastUsers, e.getEndCause()));

    }



}
