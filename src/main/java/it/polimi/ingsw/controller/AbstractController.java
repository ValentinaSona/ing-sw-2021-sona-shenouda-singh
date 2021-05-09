package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.model.Player;

import javax.swing.*;
import java.util.ArrayList;


public abstract class AbstractController {
    private Model model;

    public AbstractController(Model model){
        this.model = model;
    }
    public ArrayList<Player> getPlayersList() {
        ArrayList<Player> playersList = model.getPlayers();
        return new ArrayList<>(playersList);
    }

    public Player getCurrentPlayer() {
        return model.getCurrentPlayer();
    }

    public void setCurrentPlayer(Player player){
        model.setCurrentPlayer(player);
    }
}
