package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.controller.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameView implements Serializable {
    private static GameView singleton;
    private final ArrayList<PlayerView> players = new ArrayList<>();
    private final ArrayList<User> users;
    private MarketView marketInstance;
    private DevMarketView developmentCardsMarket;
    //da sostituire al più presto con mappa bidirezionale
    private final Map<User, PlayerView> userPlayerHashMap = new HashMap<>();
    private PlayerView currentPlayer;


    public static GameView getInstance(ArrayList<User> users){
        if(singleton == null){
            singleton = new GameView(users);
        }

        return singleton;
    }

    public static GameView getInstance(){
        if(singleton == null){
            throw new RuntimeException("this method should never be called if the game is not yet initialized");
        }

        return singleton;
    }

    public static GameView destroy(){
        if(singleton != null){
            singleton = null;
        }
        return null;
    }

    /**
     * This constructor is used when we create a new game
     *
     */
    private GameView(ArrayList<User> users){
        this.users = users;
        for(User user : users){
            userPlayerHashMap.put(user, new PlayerView(user.getNickName()));
        }
    }


    public MarketView getMarketInstance(){
        return marketInstance;
    }
    public void setMarketInstance(MarketView marketView) { marketInstance = marketView; }

    public DevMarketView getDevelopmentCardsMarket() {
        return developmentCardsMarket;
    }
    public void setDevelopmentCardsMarket(DevMarketView devMarketView) {
        developmentCardsMarket = devMarketView;
    }



    public PlayerView getPlayerFromUser(User user){
        return userPlayerHashMap.get(user);
    }

    public ArrayList<PlayerView> getPlayers() {
        return new ArrayList<>(players);
    }

    public PlayerView getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerView currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}
