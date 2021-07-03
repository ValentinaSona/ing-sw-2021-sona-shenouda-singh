package it.polimi.ingsw.server;


import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionGameSetupMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;
import it.polimi.ingsw.utils.persistence.SavedState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Match implements Runnable{
    /**
     * Reference to the current model
     */
    private Game model;
    /**
     * Boolean used by  the lobby to inform the match that the game is loaded from a file
     * and that some setup procedure are to be ignored
     */
    private boolean loadFromFile;
    /**
     * Reference to the current lobby
     */
    private final Lobby lobby;
    /**
     * Reference to the current controller
     */
    private Controller controller;
    /**
     * Map that contains all the connections with the relatives nicknames
     * */
    private final Map<String, Connection> participantMap;
    /**
     * Map that contains all the handler tha are observer of a connection
     */
    private final List<RealRemoteViewHandler> remoteViewList;
    /**
     * Reference to the current server
     */
    private final Server server;
    /**
     * Lock to safely access the content of active
     */
    private final Object activeLock = new Object();
    /**
     * Boolean used to maintain the match active
     */
    private boolean active;

    /**
     * Constructor of match
     * @param server running this match
     * @param lobby handling disconnection or connection for this game
     */
    public Match(Server server, Lobby lobby){
        this.lobby = lobby;
        this.server = server;
        this.participantMap = new ConcurrentHashMap<>();
        this.remoteViewList = new LinkedList<>();
        this.active = true;
        this.loadFromFile = false;
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

    /**
     * Called when the match is launched on a new thread and does all the required setup
     * operations before launching a game
     */
    @Override
    public void run(){

        if(!isLoadFromFile()){
            this.model = Game.getInstance(participantMap.size());
            model.setGameState(GameState.SETUP_GAME);
        }else{
            this.model = Game.getInstance();
        }
        this.controller = Controller.getInstance(model, this);

        //this list is already populated but we have yet to decide what the remoteView should do
        //when notified by the model with the notify() method
        for(RealRemoteViewHandler view : remoteViewList){

            User user = view.getUser();
            if(!isLoadFromFile()){
                model.subscribeUser(user);
            }

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
        if(isLoadFromFile()){
            controller.loadFromFile(remoteViewList);
        }else{
            controller.setup();
        }


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

    /**
     * Called by the lobby when it has to handle a reconnection to an ongoing game
     * @param nickname nickname of the player requesting the reconnection
     * @param connection connection of the reconnecting player
     */
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

    /**
     * Called by the controller when handling a disconnection of a player from a game
     * @param user user to disconnect
     */
    public void handleDisconnection(User user){
        model.disconnectPlayer(user.getNickName());
        participantMap.remove(user.getNickName());
        remoteViewList.removeIf(view-> {
            if(view.getUser().equals(user)){
                view.requestDisconnection();
                return true;
            }else {
                return false;
            }
        });

    }

    /**
     * Called when closing a game after it has ended or when something went wrong in the setup phase of a game.
     * For example a player disconnected while in setup phase
     */
    private void destroyEveryThing(){
        Game.destroy();
        Controller.destroy();
        setActive(false);
        lobby.setActiveMatch(false);
        lobby.setLobbyState(LobbyState.LOBBY_SETUP);
        remoteViewList.forEach((remoteView)-> remoteView.requestDisconnection());
        remoteViewList.clear();
        server.getLobbyThread().interrupt();
        //System.exit(0);
    }

    /**
     * Notify all the players that the game is being destroyed and then calls destroyEveryThing()
     */
    public void endGameDuringSetup(){
        model.notify(new DisconnectionGameSetupMessage());
        destroyEveryThing();
    }

    /**
     * Notify all the players that the game is ended and then calls destroyEveryThing()
     */
    public void endGame(){
        model.notify(new DisconnectionMessage());
        destroyEveryThing();
    }

    /**
     * Method called by the controller and it used to save the current game from file
     */
    public void saveToFile(){
        SavedState.save(Game.getInstance());
        Game.destroy();
        Controller.destroy();
        setActive(false);
        setLobbyState(LobbyState.LOBBY_SETUP);
        server.getLobbyThread().interrupt();
        remoteViewList.forEach((remoteView)-> remoteView.requestDisconnection());
        remoteViewList.clear();
    }

    /**
     * Used to communicate with the lobby. Used for example when the lobby has to pass from waiting for disconnected players to
     * the creation of a new game
     * @param state state in witch the lobby has to move
     */
    public void setLobbyState(LobbyState state){
        switch (state){
            case LOBBY_SETUP -> {
                lobby.setActiveMatch(false);
                setActive(false);
                lobby.setLobbyState(state);
            }
            case GAME_SETUP -> setLobbyState(state);
            case IN_GAME -> {
                lobby.setActiveMatch(true);
                lobby.setLobbyState(state);

            }

        }

    }

    public boolean isLoadFromFile() {
        return loadFromFile;
    }

    public void setLoadFromFile(boolean loadFromFile) {
        this.loadFromFile = loadFromFile;
    }
}
