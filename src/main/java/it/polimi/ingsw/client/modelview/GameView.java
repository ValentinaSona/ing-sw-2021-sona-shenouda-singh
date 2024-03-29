package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.controller.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class that is the view equivalent of the Game class.
 * Holds the markets' views, the players' views and the value of Lorenzo's black cross.
 */
public class GameView implements Serializable {
    private static GameView singleton;

    /**
     * Market view of the game
     */
    private MarketView marketInstance;
    /**
     * Development cards market of the game.
     */
    private DevMarketView developmentCardsMarket;
    /**
     * Value of Lorenzo's faith track for solo games.
     */
    private int blackCross;
    /**
     * List mapping users to their view.
     */
    private final Map<User, PlayerView> userPlayerHashMap = new HashMap<>();
    /**
     * The view of the player playing their turn.
     */
    private PlayerView currentPlayer;




    public static GameView getInstance(List<User> users){
        if(singleton == null){
            singleton = new GameView(users);
        }

        return singleton;
    }

    public static GameView getInstance(){
        if(singleton == null){
            throw new RuntimeException("This method should never be called if the game is not yet initialized");
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
     * This constructor is used when we create a new game.
     */
    private GameView(List<User> users){
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
    public void addBlackCross(int blackCross) {
        this.blackCross += blackCross;
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

    public ArrayList<PlayerView> getPlayers() { return new ArrayList<>(userPlayerHashMap.values()); }

    public PlayerView getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(PlayerView currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void updatePlayerViews(List<PlayerView> playerViews) {
        if (playerViews.size() != userPlayerHashMap.size()) return;

        for (PlayerView player : playerViews){
            userPlayerHashMap.replace(new User(player.getNickname()), player);
        }
    }
}
