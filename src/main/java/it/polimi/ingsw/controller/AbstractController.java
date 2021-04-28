package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Player;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;


public abstract class AbstractController implements PropertyChangeListener {
    private Player currentPlayer;
    private ArrayList<Player> playersList;
    public static String BUY_MARBLES = "Buy_Marbles";
    public static String CONVERT_WHITE_MARBLES = "Convert_White_Marbles";

    public ArrayList<Player> getPlayersList() {
        return playersList;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayersList(ArrayList<Player> playersList) {
        this.playersList = playersList;
    }
}
