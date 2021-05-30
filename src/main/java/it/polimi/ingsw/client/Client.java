package it.polimi.ingsw.client;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Connection;

public class Client {
    private Connection connection;
    private String nickname;
    private Ui chosenUi;
    private GameView gameView;
    private User currentActiveUser;

    public Client(Ui uiInterface){
        this.chosenUi = uiInterface;
    }

    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public Ui getChosenUi() {
        return chosenUi;
    }


    public GameView getGameView() {
        return gameView;
    }
    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }


    public User getCurrentActiveUser() {
        return currentActiveUser;
    }
    public void setCurrentActiveUser(User currentActiveUser) {
        this.currentActiveUser = currentActiveUser;
    }
}
