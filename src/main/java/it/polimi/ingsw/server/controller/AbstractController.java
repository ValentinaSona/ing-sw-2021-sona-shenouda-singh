package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.model.Player;

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

    public Model getModel() {
        return model;
    }
}
