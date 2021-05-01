package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;
import java.util.ArrayList;


public abstract class AbstractController {
    private static Player currentPlayer;
    private static ArrayList<Player> playersList;


    public ArrayList<Player> getPlayersList() {
        return new ArrayList<>(playersList);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        AbstractController.currentPlayer = currentPlayer;
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        AbstractController.playersList = playersList;
    }
}
