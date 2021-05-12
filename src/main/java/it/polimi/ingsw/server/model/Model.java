package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    private static Model singleton;
    private final int numOfPlayers;
    private final ArrayList<Player> players = new ArrayList<>();
    private final Market marketInstance;
    private final DevelopmentCardsMarket developmentCardsMarket;
    private final HashMap<User, Player> subscribedUsers = new HashMap<>();
    private Player currentPlayer;
    public static Model getInstance(int numberOfPlayers){
        if(singleton == null){
            singleton = new Model(numberOfPlayers);
        }

        return singleton;
    }

    /**
     * This constructor is used when we create a new game
     *
     */
    private Model(int numberOfPlayers){
        numOfPlayers = numberOfPlayers;
        marketInstance = MarketBuilder.build();
        developmentCardsMarket = DevelopmentBuilder.build();
    }


    public void subscribeUser(User user){
        if(subscribedUsers.size() == numOfPlayers){
            new RuntimeException("Sto inserendo più player di quelli consentiti");
        }
        Player player = new Player(user.nickName);
        subscribedUsers.put(user, player);
        players.add(player);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
