package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Game extends LambdaObservable<Transmittable> {
    private static Game singleton;
    private final int numOfPlayers;
    private final ArrayList<Player> players = new ArrayList<>();
    private final Market marketInstance;
    private final LeaderCardsKeeper leaderCardsKeeper;
    private final DevelopmentCardsMarket developmentCardsMarket;
    //da sostituire al più presto con mappa bidirezionale
    private final Map<User, Player> userPlayerHashMap = new HashMap<>();
    private final Map<Player, User> playerUserHashMap = new HashMap<>();
    private Player currentPlayer;
    private boolean active;
    private GameState gameState;



    public static Game getInstance(int numberOfPlayers){
        if(singleton == null){
            singleton = new Game(numberOfPlayers);
        }

        return singleton;
    }

    public static Game destroy(){
        if(singleton != null){
            singleton = null;
        }
        return singleton;
    }

    /**
     * This constructor is used when we create a new game
     *
     */
    private Game(int numberOfPlayers){
        this.numOfPlayers = numberOfPlayers;
        this.marketInstance = MarketBuilder.build();
        this.developmentCardsMarket = DevelopmentBuilder.build();
        this.leaderCardsKeeper = new LeaderCardsKeeper();
        this.gameState = GameState.SETUP_GAME;
    }

    public Market getMarketInstance(){
        return marketInstance;
    }

    public DevelopmentCardsMarket getDevelopmentCardsMarket() {
        return developmentCardsMarket;
    }

    public LeaderCardsKeeper getLeaderCardsKeeper() {
        return leaderCardsKeeper;
    }

    public void subscribeUser(User user){
        if(userPlayerHashMap.size() == numOfPlayers){
            throw new RuntimeException("Sto inserendo più player di quelli consentiti");
        }
        Player player = new Player(user.nickName);
        userPlayerHashMap.put(user, player);
        playerUserHashMap.put(player, user);
        players.add(player);
    }

    public Player getPlayerFromUser(User user){
        return userPlayerHashMap.get(user);
    }

    public User getUserFromPlayer(Player player){return playerUserHashMap.get(player);}

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}

