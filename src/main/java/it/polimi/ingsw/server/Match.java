package it.polimi.ingsw.server;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;

public class Match {
    private Model model;
    private Controller controller;
    public void start(){
        int numOfPlayer = 3;
        this.model = Model.getInstance(numOfPlayer);
        this.controller = Controller.getInstance(model);
        //deveo creare la view e metterla in ascolto del model

    }
}
