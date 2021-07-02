package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.MockRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.persistenza.ClientLoadGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.persistenza.ClientSaveGameMessage;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.*;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;


import java.io.IOException;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the class through which bot cli and gui interact with the server.
 * Its methods are wrappers for the various messages the server expects to receive, so that both cli and gui are ensured to communicate consistently.
 */

public class UIController implements LambdaObserver{

    private final Logger LOGGER = Logger.getLogger(UIController.class.getName());
    private static UIController singleton;
    private final Thread dispatcherThread;
    /**
     * Connection to the server.
     */
    private Connection clientConnection;
    private Thread localGame;
    /**
     * Used in local solo games to maintain the message-like infrastructure.
     */
    private MockRemoteViewHandler view;
    /**
     * Whether the game is local or not.
     */
    private boolean local = false;


    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {
        dispatcherThread = new Thread(DispatcherController.getInstance());
        dispatcherThread.start();
    }

    /**
     * Called instead of the setNickname, joinLobby and setCreation methods when starting a local game.
     * Creates a mock remote view so that all the messages and the structures of the remote games can be used for the local ones.
     * @param nickname the player's nickname.
     */
    public void startLocalSinglePlayerGame(String nickname){

        MatchSettings.getInstance().setClientNickname(nickname);
        local = true;
        MatchSettings.getInstance().setSolo(true);

        Runnable runnable = ()-> {
            Game model = Game.getInstance(1);

            Controller controller = Controller.getInstance(model);

            User user = new User(nickname);

            model.subscribeUser(user);

            view = new MockRemoteViewHandler(nickname,
                    DispatcherController.getInstance());

            model.addObserver(view, (observer, transmittable)->{
                if(transmittable instanceof DisconnectionMessage){
                    ((RemoteViewHandler) observer).requestDisconnection();
                }else {
                    ((RemoteViewHandler) observer).updateFromGame(transmittable);
                }
            });


            view.addObserver(controller, (observer, viewClientMessage) ->
                    ((Controller) observer).update(viewClientMessage) );

            controller.setup();



            while (true){
                try{
                    controller.dispatchViewClientMessage();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        localGame = new Thread(runnable);
        localGame.start();
    }

    /**
     * Acts as wrapper around the send function so the methods invoke this whether game is local or remote.
     * @param message the message being transmitted to the server/view.
     */
    private void send(Transmittable message){
        if(local){
            view.updateFromClient(message);
        }else{
            clientConnection.send(message);
        }
    }

    /* ********************************************************************
     * Methods invoked by the UIs to send various messages to the server. *
     **********************************************************************/

    /**
     * Invoked by cli and gui after nickname selection to make first contact with server.
     * Creates a ClientSetNicknameMessage and establishes connection.
     * @param nickname user's chosen nickname.
     * @param host hostname of the server.
     * @param port port on which server is running.
     * @throws IOException If there are problems with the socket.
     */
    public void sendNickname(String nickname, String host, int port) throws IOException {
        local = false;
        MatchSettings.getInstance().setClientNickname(nickname);
        DispatcherController dispatcherController = DispatcherController.getInstance();

        try {
            Socket clientSocket = new Socket(host, port);
            clientConnection = new Connection(clientSocket);
            clientConnection.addObserver(dispatcherController, (observer, transmittable) -> {
                ((DispatcherController) observer).update(transmittable);
            });

            // Send the message
            Thread t = new Thread(clientConnection);
            t.start();
            Transmittable message = new ClientSetNicknameMessage(nickname);
            send(message);
        }catch (IOException exception){
            LOGGER.log(Level.SEVERE, "Socket Opening failed "+exception.getMessage());
            throw exception;
        }
    }

    public void joinLobby() {
        send(new ClientJoinLobbyMessage());
    }

    /**
     * Called by the first player upon receiving the SET_COUNT message.
     * @param playersNum the number of players expected to start the game.
     */
    public void setCreation(int playersNum) {
        send(new ClientSetPlayersCountMessage(playersNum));
        MatchSettings.getInstance().setTotalUsers(playersNum);
        if (playersNum ==1) MatchSettings.getInstance().setSolo(true);
    }

    /**
     * Method called by UI after choosing the two leader cards and the resources from initial selection.
     * @param idResourceMap Maps resources to deposits.
     * @param chosen contains the two leader cards.
     */
    public void chosenStartingResources(Map<Id, Resource> idResourceMap, LeaderCard[] chosen) {
        String nickname = MatchSettings.getInstance().getClientNickname();
        if(idResourceMap == null){
            idResourceMap = new HashMap<>();
        }
        send(new ClientSetupActionMessage(idResourceMap,
                chosen,
                new User(nickname)));
    }

    public void tidyWarehouse(Id from, Id to){
        send(new ClientTidyWarehouseMessage(from,to));
    }

    public void buyMarbles(int rowCol){
        send(new ClientBuyMarblesMessage(rowCol));
    }

    public void convertWhiteMarbles(MarketMarble[] choices){send(new ClientConvertWhiteMarblesMessage(choices));}

    public void depositIntoWarehouse(Id id, Resource resource){send( new ClientDepositIntoWarehouseMessage(id, resource));}

    public void throwResources(){send( new ClientThrowResourcesMessage());}

    public void endTurn(){
        send(new ClientEndTurnMessage());
    }

    public void activateSpecialAbility(Id id){ send(new ClientActivateSpecialAbilityMessage(id));}

    public void throwLeaderCard(Id id){ send(new ClientThrowLeaderCardMessage(id));}

    public void selectDevelopmentCard(int row, int col, Id slot){ send(new ClientSelectDevelopmentCardMessage(row, col,slot));}

    public void depositResourcesIntoSlot(Id slot,  Map<Id, Resource> map){send(new ClientDepositResourceIntoSlotMessage(slot, map));}

    public void depositResourcesIntoSlot(Id slot, Map<Id, Resource> map, ResourceType resourceType, Boolean card){send(new ClientDepositResourceIntoSlotMessage(slot, map, resourceType, card));}

    public void buyTargetCard(Id id){send(new ClientBuyTargetCardMessage(id));}

    public void activateProduction(){send(new ClientActivateProductionMessage());}

    public void disconnectFromServer(){
        send(new DisconnectionMessage());
    }

    /**
     * Called instead of joinLobby() after receiving the OK_NICK when restoring a game from file.
     * Sets the loadFromGame parameter to true, used by Lobby.handleJoiningRequest() to differentiate new games from restored ones.
     * If it fails, a disconnectionMessage is received.
     */
    public void resumeGameFromFile(){
        send(new ClientJoinLobbyMessage(true));
    }

    /**
     * Called instead of setCreation upon receiving the SET_COUNT when restoring a game from file (only by the first player).
     * Sets the size to the one of the original game.
     * If it succeeds OK_COUNT is received, while CLIENT_ERROR is received in case of failure.
     * Once everyone has joined, a ServerReconnectionMessage followed by the appropriate SeverStartTurnMessage are received.
     */
    public void loadGameFromFile(){send(new ClientLoadGameMessage());}


    /**
     * For ease of testing only! This special message triggers the end of the game, no matter the stage.
     * Called by the 1492 hidden code.
     */
    public void endGame(){send(new ClientEndGameMessage());}


    /**
     * To be called by the current player during their turn if they wish to save the game.
     * All players will be disconnected as result.
     */
    public void saveGame(){send(new ClientSaveGameMessage());}

    /**
     * Called when attempting to reconnect to a game after closing the client or losing the connection.
     * A RECONNECTION_OK status message will be received if the reconnection is successful, followed by a ServerGameReconnectionMessage.
     * Otherwise a DisconnectionMessage will close the newly reopened connection if something went wrong.
     * @param nickname the nickname under which the player is trying to reconnect
     * @param host the server hostname
     * @param port the server port
     * @throws IOException When failing to open the socket.
     */
    public void reconnectToServer(String nickname, String host, int port) throws IOException{
        local = false;
        MatchSettings.getInstance().setClientNickname(nickname);
        DispatcherController dispatcherController = DispatcherController.getInstance();

        try {
            Socket clientSocket = new Socket(host, port);
            clientConnection = new Connection(clientSocket);
            clientConnection.addObserver(dispatcherController, (observer, transmittable) -> {
                ((DispatcherController) observer).update(transmittable);
            });
            // Send the message
            Thread t = new Thread(clientConnection);
            t.start();
            Transmittable message = new ClientGameReconnectionMessage(nickname);
            send(message);
        }catch (IOException exception){
            LOGGER.log(Level.SEVERE, "Failed to open socket "+exception.getMessage());
            throw exception;
        }
    }

    public Connection getClientConnection() {
        return clientConnection;
    }

    public boolean isLocal() {
        return local;
    }
}
