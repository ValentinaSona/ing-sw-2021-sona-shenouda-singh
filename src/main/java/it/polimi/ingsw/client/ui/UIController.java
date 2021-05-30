package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;

import java.io.IOException;
import java.net.Socket;

/**
 * This should be a universal controller called by the ui
 */

public class UIController implements LambdaObserver{

    private static UIController singleton;
    private Connection clientConnection;
    private UiControllerInterface currentController;
    //devo capire come  passargli il client
    private Client client;

    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {};

    public void sendNickname(UiControllerInterface controller, String nickname, String host, int port) throws IOException {
        System.out.println("Voglio mandare nickname..");
        currentController = controller;
        //MatchSettings.getInstance().setClientNickname(nickname);
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
        System.out.println("Voglio joinare lobby..");
        currentController = controller;
        clientConnection.send(new ClientJoinLobbyMessage());
    }

    public void setCreation(UiControllerInterface controller, int playersNum) {
        currentController = controller;
        clientConnection.send(new ClientSetPlayersCountMessage(playersNum));
        MatchSettings.getInstance().setPlayersNum(playersNum);
        MatchSettings.getInstance().addPlayer(MatchSettings.getInstance().getClientNickname());

    }

    public String getClientNickname() {
        return  MatchSettings.getInstance().getClientNickname();
    }

    public int currentPlayerNum() {
        return MatchSettings.getInstance().getJoiningPlayers().size();
    }

    public int totalPlayerNum() {
        return MatchSettings.getInstance().getPlayersNum();
    }

    public void update(Transmittable serverMessage){
        currentController.handleMessage(serverMessage);
    }
}
