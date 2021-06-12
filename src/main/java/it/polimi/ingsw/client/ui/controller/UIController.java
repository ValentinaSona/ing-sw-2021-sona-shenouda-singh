package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.view.MockRemoteViewHandler;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.*;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;


import java.io.IOException;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the class through which bot cli and gui interact with the server.
 * Its methods are wrappers for the various messages the server expects to receive, so that both cli and gui are ensured to communicate consistently.
 */

public class UIController implements LambdaObserver{

    private static UIController singleton;
    private Thread dispatcherThread;
    private Connection clientConnection;
    private Thread localGame;
    private MockRemoteViewHandler view;
    private boolean local = false;


    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {
        dispatcherThread = new Thread(DispatcherController.getInstance());
        dispatcherThread.start();
    }

    public void startLocalSinglePlayerGame(String nickname){

        MatchSettings.getInstance().setClientNickname(nickname);
        local = true;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Game model = Game.getInstance(1);

                Controller controller = Controller.getInstance(model);

                User user = new User(nickname);

                model.subscribeUser(user);

                view = new MockRemoteViewHandler(nickname,
                        UIController.getInstance(),
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

                //TODO: this as an alternate method to EndOfGame?
                model.setActive(true);

                while (model.isActive()){
                    try{
                        System.out.println("Sono entrato");
                        controller.dispatchViewClientMessage();
                    }catch (Exception e) {
                        e.printStackTrace();
                        //devo gestire l'errore disconnettendo tutti i giocatori
                    }
                }
            }
        };
        localGame = new Thread(runnable);
        localGame.start();
    }

    private void send(Transmittable message){
        if(local){
            view.updateFromClient(message);
        }else{
            clientConnection.send(message);
        }
    }

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

        Socket clientSocket = new Socket(host, port);
        clientConnection = new Connection(clientSocket);
        clientConnection.addObserver(dispatcherController, (observer, transmittable) -> {
            ((DispatcherController) observer).update(transmittable);
        });
        //mando il messaggio
        Thread t = new Thread(clientConnection);
        t.start();
        Transmittable message = (Transmittable) new ClientSetNicknameMessage(nickname);
        send(message);
    }

    public void joinLobby() {
        send((Transmittable) new ClientJoinLobbyMessage());
    }

    public void setCreation(int playersNum) {
        send((Transmittable) new ClientSetPlayersCountMessage(playersNum));
        MatchSettings.getInstance().setTotalUsers(playersNum);

    }
    // Metodo che viene chiamato dalla UI quando il giocatore ha scelto le 2 leader cards da tenere
    public void chosenStartingResources(Map<Id, Resource> idResourceMap, LeaderCard[] chosen) {
        String nickname = MatchSettings.getInstance().getClientNickname();
        if(idResourceMap == null){
            idResourceMap = new HashMap<>();
        }
        send((Transmittable) new ClientSetupActionMessage(idResourceMap,
                chosen,
                new User(nickname)));
    }

    public void tidyWarehouse(Id from, Id to){
        send((Transmittable) new ClientTidyWarehouseMessage(from,to));
    }

    public void buyMarbles(int rowCol){
        send((Transmittable) new ClientBuyMarblesMessage(rowCol));
    }

    public void depositIntoWarehouse(Id id, Resource resource){
        send((Transmittable) new ClientDepositIntoWarehouseMessage(id, resource));}

    public void throwResources(){
        send((Transmittable) new ClientThrowResourcesMessage());}

    public void endTurn(){
        send((Transmittable) new ClientEndTurnMessage());
    }

    public void activateSpecialAbility(Id id){ send((Transmittable) new ClientActivateSpecialAbilityMessage(id));}


    public void throwLeaderCard(Id id){ send((Transmittable) new ClientThrowLeaderCardMessage(id));}
}
