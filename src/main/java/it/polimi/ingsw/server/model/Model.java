package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.observable.Player;
import it.polimi.ingsw.server.view.RemoteViewHandler;

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
            throw new RuntimeException("Sto inserendo più player di quelli consentiti");
        }
        Player player = new Player(user.nickName);
        userPlayerHashMap.put(user, player);
        playerUserHashMap.put(player, user);
        players.add(player);
    }

    //TODO DEVO ANDDARE A FARE IN MODO CHE TUTTE LE CLASSI DEL MODEL CHE IMPLEMENTANO LAMBDA OBSERVABLE VADANO AD AGGIUNGERE LA VIEW COME OBSERVER
    public void addObserver(RemoteViewHandler view){
        //TODO devo andare a chiamare su tutte le classi nel package observable addObserver(view, lambdaFunction)
        //TODO inolre vedo se forse conviene che il player gestisca tutte le sue cose del model
        //TODO in modo tale che sia solo lei la classe che estende lambdaObservable??
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


    //TODO fase di setup in cui vengono mandati ai player la richiesta della scelta delle carte e
    //delle risorse iniziali
    public void setup(){}
}
