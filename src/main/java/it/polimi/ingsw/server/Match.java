package it.polimi.ingsw.server;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionGameSetupMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Match implements Runnable{
    private Game model;
    private Lobby lobby;
    private Controller controller;
    private final Map<String, Connection> participantMap;
    private final List<RealRemoteViewHandler> remoteViewList;
    private final Server server;
    private Object activeLock = new Object();
    private boolean active;


    public Match(Server server, Lobby lobby){
        this.lobby = lobby;
        this.server = server;
        this.participantMap = new ConcurrentHashMap<>();
        this.remoteViewList = new LinkedList<>();
        this.active = true;
    }

    public void addParticipant(String nickname, Connection connection) {
        participantMap.put(nickname, connection);
        remoteViewList.add(new RealRemoteViewHandler(connection, nickname));
    }

    /**
     * Returns the participants to the match, represented by their nickname and connection
     * in order to remove the ConnectionSetupHandler from the observer of the connection of the players
     * in the game
     * @return the participants to the match
     */
    public Map<String, Connection> getParticipantMap() {
        return participantMap;
    }

    @Override
    public void run(){
        this.model = Game.getInstance(participantMap.size());
        model.setGameState(GameState.SETUP_GAME);
        this.controller = Controller.getInstance(model, this);

        //this list is already populated but we have yet to decide what the remoteView should do
        //when notified by the model with the notify() method
        for(RealRemoteViewHandler view : remoteViewList){

            User user = view.getUser();

            model.subscribeUser(user);

            model.addObserver(view, (observer, transmittable)->{
                if(transmittable instanceof DisconnectionMessage){
                    ((RealRemoteViewHandler)observer).requestDisconnection();
                }else {
                    ((RealRemoteViewHandler) observer).updateFromGame(transmittable);
                }
            });


            view.addObserver(controller, (observer, viewClientMessage) ->
                    ((Controller) observer).update(viewClientMessage) );
        }

        controller.setup();

        while (isActive()){
            try{
                controller.dispatchViewClientMessage();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public boolean isActive() {
        synchronized (activeLock){
            return active;
        }
    }

    public void setActive(boolean active) {
        synchronized (activeLock){
            this.active = active;
        }
    }

    public void handleReconnection(String nickname, Connection connection){
        participantMap.put(nickname, connection);
        RealRemoteViewHandler view = new RealRemoteViewHandler(connection, nickname);
        remoteViewList.add(view);

        model.addObserver(view, (observer, transmittable)->{
            if(transmittable instanceof DisconnectionMessage){
                ((RealRemoteViewHandler)observer).requestDisconnection();
            }else {
                ((RealRemoteViewHandler) observer).updateFromGame(transmittable);
            }
        });


        view.addObserver(controller, (observer, viewClientMessage) ->
                ((Controller) observer).update(viewClientMessage) );

        controller.handleReconnection(view, view.getUser());
    }

    public void handleDisconnection(User user){
        model.disconnectPlayer(user.getNickName());
        participantMap.remove(user.getNickName());
        remoteViewList.removeIf(view-> view.getUser().equals(user));
    }

    public void endGame(){
        model.notify(new DisconnectionGameSetupMessage());
        Game.destroy();
        Controller.destroy();
        setActive(false);
        lobby.setActiveMatch(false);
        lobby.setLobbyState(LobbyState.LOBBY_SETUP);
        remoteViewList.forEach((remoteView)-> remoteView.requestDisconnection());
    }

    public void setLobbyState(LobbyState state){
        lobby.setLobbyState(state);
        lobby.setActiveMatch(true);
    }
}
