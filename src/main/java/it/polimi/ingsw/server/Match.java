package it.polimi.ingsw.server;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.Model;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Match implements Runnable{
    private Model model;
    private Controller controller;
    private final Map<String, Connection> participantMap;
    private final List<RemoteViewHandler> remoteViewList;
    private final Server server;

    public Match(Server server){
        this.server = server;
        this.participantMap = new ConcurrentHashMap<>();
        this.remoteViewList = new LinkedList<>();
    }

    public void start(){
        int numOfPlayer = 3;
        this.model = Model.getInstance(numOfPlayer);
        this.controller = Controller.getInstance(model);
        //deveo creare la view e metterla in ascolto del model

    }

    public void addParticipant(String nickname, Connection connection) {
        participantMap.put(nickname, connection);
        remoteViewList.add(new RemoteViewHandler(connection, nickname));
    }
}
