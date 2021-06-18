package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.controller.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameView implements Serializable {
    private static GameView singleton;

    private final ArrayList<User> users;
    private MarketView marketInstance;
    private DevMarketView developmentCardsMarket;
    private int blackCross;
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
        blackCross = 0;
    }

    public int getBlackCross() {
        return blackCross;
    }

    public void setBlackCross(int blackCross) {
        this.blackCross = blackCross;
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

        return new ArrayList<>(userPlayerHashMap.values());
    }

    public PlayerView getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerView currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

}
