package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.observer.LambdaObserver;


import java.io.IOException;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;

/**
 * This should be a universal controller called by the ui
 */

public class UIController implements LambdaObserver{

    private static UIController singleton;
    private Connection clientConnection;


    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {};


    public void sendNickname(String nickname, String host, int port) throws IOException {
        MatchSettings.getInstance().setClientNickname(nickname);
        DispatcherController dispatcherController = DispatcherController.getInstance();

        Socket clientSocket = new Socket(host, port);
        clientConnection = new Connection(clientSocket);
        clientConnection.addObserver(dispatcherController, (observer, transmittable)->{
            ((DispatcherController) observer).update(transmittable);
        });
        //mando il messaggio
        Thread t = new Thread(clientConnection);
        t.start();
        Thread t1 = new Thread(dispatcherController);
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
        if(idResourceMap == null){
            idResourceMap = new HashMap<>();
        }
        clientConnection.send(new ClientSetupActionMessage(idResourceMap,
                chosen,
                new User(nickname)));
    }




}
