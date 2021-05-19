package it.polimi.ingsw.server;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
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
    private boolean active;


    public Match(Server server){
        this.server = server;
        this.participantMap = new ConcurrentHashMap<>();
        this.remoteViewList = new LinkedList<>();
        this.active = true;
    }

    public void addParticipant(String nickname, Connection connection) {
        participantMap.put(nickname, connection);
        remoteViewList.add(new RemoteViewHandler(connection, nickname));
    }

    /**
     * Returns the participants to the match, represented by their nickname and connection
     * in order to remove the ConnectionSetupHandler from the observer of the connection of the players
     * in the game
     * @return the participants to the match
     */
    public Map<String, Connection> getParticipantMap() {
        return new ConcurrentHashMap<>(participantMap);
    }

    @Override
    public void run(){
        this.model = Model.getInstance(participantMap.size());

        this.controller = Controller.getInstance(model);

        //this list is already populated but we have yet to decide what the remoteView should do
        //when notified by the model whith the notify() method
        for(RemoteViewHandler view : remoteViewList){

            User user = view.getUser();

            model.subscribeUser(user);

            model.addObserver(view);

            view.addObserver(controller, (observer, viewClientMessage) ->
                    ((Controller) observer).update(viewClientMessage) );
        }

        model.setup();
        //now just make the controller work on this thread
        while (active) {
            try {
                this.controller.dispatchViewClientMessages();
            } catch (Exception e) {
               //devo capire come gestire questo caso
            }
            //TODO check if the game is active and then set active of controller to false if the game is not active

        }
    }
}
