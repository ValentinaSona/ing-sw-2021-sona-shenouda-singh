package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.view.ViewClientMessage;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ControllerHandleable;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
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

    public void sendNickname(UiControllerInterface controller, String nickname, String host, int port) throws IOException {
        currentController = controller;
        MatchSettings.getInstance().setClientNickname(nickname);
        Socket clientSocket = new Socket(host, port);
        clientConnection = new Connection(clientSocket);
        clientConnection.addObserver(this, (observer, transmittable)->{
            ((UIController) observer).update(transmittable);
        });
        //mando il messaggio
        Thread t = new Thread(clientConnection);
        t.start();
        clientConnection.send(new ClientSetNicknameMessage(nickname));
    }

    public void joinLobby(UiControllerInterface controller) {
        currentController = controller;
        clientConnection.send(new ClientJoinLobbyMessage());
    }

    public void setCreation(UiControllerInterface controller, int playersNum) {
        currentController = controller;
        clientConnection.send(new ClientSetPlayersCountMessage(playersNum));
        MatchSettings.getInstance().setTotalUsers(playersNum);

    }
    // Metodo che viene chiamato dalla UI quando il giocatore ha scelto le 2 leader cards da tenere
    public void chosenLeader(UiControllerInterface controller, List<LeaderCard> chosenCards) {}

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
            currentController.handleMessage(message);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

}
