package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.User;

import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    private static Model singleton;
    private final int numOfPlayers;
    private final ArrayList<Player> players = new ArrayList<>();
    private final Market marketInstance;
    private final DevelopmentCardsMarket developmentCardsMarket;
    //da sostituire al più presto con mappa bidirezionale
    private final HashMap<User, Player> userPlayerHashMap = new HashMap<>();
    private final HashMap<Player, User> playerUserHashMap = new HashMap<>();
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
        if(userPlayerHashMap.size() == numOfPlayers){
            new RuntimeException("Sto inserendo più player di quelli consentiti");
        }
        Player player = new Player(user.nickName);
        userPlayerHashMap.put(user, player);
        playerUserHashMap.put(player, user);
        players.add(player);
    }

    public Player getPlayerFromUser(User user){
        return userPlayerHashMap.get(user);
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
