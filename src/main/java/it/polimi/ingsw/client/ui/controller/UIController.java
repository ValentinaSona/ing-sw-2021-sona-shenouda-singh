package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.ClientHandleable;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupActionMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * This should be a universal controller called by the ui
 */

public class UIController implements LambdaObserver{

    private static UIController singleton;
    private Connection clientConnection;
    private UiControllerInterface currentController;
    private final BlockingQueue<Transmittable> serverMessages= new LinkedBlockingDeque<>();

    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {};

    public void setCurrentController(UiControllerInterface currentController) {

        this.currentController = currentController;

    }

    public void sendNickname(String nickname, String host, int port) throws IOException {
        MatchSettings.getInstance().setClientNickname(nickname);
        Socket clientSocket = new Socket(host, port);
        clientConnection = new Connection(clientSocket);
        clientConnection.addObserver(this, (observer, transmittable)->{
            ((UIController) observer).update(transmittable);
        });
        //mando il messaggio
        Thread t = new Thread(clientConnection);
        t.start();
        Task<Void> waitingForResponse = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true){
                    try{
                        processServerMessage();
                    }catch (Exception e){
                        //TODO gestione disconnessioni
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t1 = new Thread(waitingForResponse);
        t1.start();
        clientConnection.send(new ClientSetNicknameMessage(nickname));
    }

    public void joinLobby() {
        clientConnection.send(new ClientJoinLobbyMessage());
    }

    public void setCreation(int playersNum) {
        clientConnection.send(new ClientSetPlayersCountMessage(playersNum));
        MatchSettings.getInstance().setTotalUsers(playersNum);

    }
    // Metodo che viene chiamato dalla UI quando il giocatore ha scelto le 2 leader cards da tenere
    public void chosenStartingResources(Map<Id, Resource> idResourceMap, LeaderCard[] chosen) {
        String nickname = MatchSettings.getInstance().getClientNickname();
        clientConnection.send(new ClientSetupActionMessage(idResourceMap,
                chosen,
                new User(nickname)));
    }

    public void update(Transmittable serverMessage){
        try{
            this.serverMessages.put(serverMessage);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    public void processServerMessage(){
        try{
            Transmittable message = this.serverMessages.take();
            if(message instanceof StatusMessage){
                Platform.runLater(()->currentController.handleStatusMessage((StatusMessage)message));
            }else{
                ClientHandleable handleable = (ClientHandleable) message;
                Platform.runLater(()->((ClientHandleable) message).handleMessage(currentController));
            }
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
