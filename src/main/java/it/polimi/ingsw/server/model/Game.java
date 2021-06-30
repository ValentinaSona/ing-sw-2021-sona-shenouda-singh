package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.observer.LambdaObservable;
import it.polimi.ingsw.utils.persistence.SavedState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends LambdaObservable<Transmittable> {
    private static Game singleton;
    private final int numOfPlayers;
    private List<Player> players = new ArrayList<>();
    private Market marketInstance;
    private final LeaderCardsKeeper leaderCardsKeeper;
    private DevMarket developmentCardsMarket;
    //da sostituire al più presto con mappa bidirezionale
    private final Map<User, Player> userPlayerHashMap = new HashMap<>();
    private final Map<Player, User> playerUserHashMap = new HashMap<>();
    private Player currentPlayer;
    private GameState gameState;
    private final boolean solo;
    private Lorenzo Lorenzo;



    public static Game getInstance(int numberOfPlayers){
        if(singleton == null){
            singleton = new Game(numberOfPlayers);
        }

        return singleton;
    }

    public static Game getInstance() {
        return singleton;
    }

    public static void restore(SavedState savedState) {

        if (singleton != null)
            throw new RuntimeException("Game already initialized, cannot load saved game!");

        else
            singleton = new Game(savedState);

    }

    public static Game destroy(){
        if(singleton != null){
            singleton = null;
        }
        return null;
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

        if (numberOfPlayers==1){
            // If the game is a single player
            this.solo = true;
            this.Lorenzo = new Lorenzo();

        } else {

            this.solo = false;
            this.Lorenzo = null;
        }
    }

    public Lorenzo getLorenzo() {
        return Lorenzo;
    }

    public boolean isSolo() {
        return solo;
    }

    private Game(SavedState savedState){

        this.numOfPlayers = savedState.numOfPlayers();
        this.marketInstance = MarketBuilder.build(
                savedState.getSavedMarket().getSavedTray(),
                savedState.getSavedMarket().getSavedExtra(),
                savedState.getMarketMap());
        this.developmentCardsMarket = DevelopmentBuilder.build(
                savedState.getSavedDevMarket().getSavedDecks(),
                savedState.getDevMap()
        );
        this.leaderCardsKeeper = new LeaderCardsKeeper();
        this.players = savedState.getSavedPlayers();
        this.currentPlayer = savedState.currentPlayer();
        if(!currentPlayer.getTurn()){
            currentPlayer.toggleTurn();
        }
        if(!currentPlayer.getMainAction()){
            currentPlayer.toggleMainAction();
        }
        players.forEach(player -> {
            playerUserHashMap.put(player, new User(player.getNickname()));
            userPlayerHashMap.put(new User(player.getNickname()), player);
        });

        if (savedState.numOfPlayers() == 1){
            // If the game is a single player
            this.solo = true;
            this.Lorenzo = savedState.getLorenzo();

        } else {

            this.solo = false;
            this.Lorenzo = null;
        }

        this.gameState = GameState.PLAY;

    }

    public Market getMarketInstance(){
        return marketInstance;
    }
    public void setMarketInstance(Market market) { marketInstance = market; }

    public DevMarket getDevelopmentCardsMarket() {
        return developmentCardsMarket;
    }
    public void setDevelopmentCardsMarket(DevMarket devMarket) {
        developmentCardsMarket = devMarket;
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

    public void disconnectPlayer(String nickname){
        getPlayers().forEach(player -> {
            if(player.getNickname().equals(nickname)){
                player.setDisconnected(true);
            }
        });
    }

    public List<User> getDisconnectedPlayers(){
        List<User> users = new ArrayList<>();
        getPlayers().forEach(player -> {
            if(player.isDisconnected()){
                users.add(getUserFromPlayer(player));
            }
        });

        return users;
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

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

}
