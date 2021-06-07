package it.polimi.ingsw.utils.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.server.exception.NotDecoratedException;
import it.polimi.ingsw.server.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SavedState {

    private static final String pathToSavedGame = ""; //TODO IMPORTANTE!!! DA DEFINIRE ALTRIMENTI NON FUNZIONA NULLA

    private final PlayersOrder order;
    private final SavedResourceMarket savedMarket;
    private final SavedDevMarket savedDevMarket;
    private final List<Player> savedPlayers;

    public SavedState(PlayersOrder order, SavedResourceMarket savedMarket, SavedDevMarket savedDevMarket, List<PlayerView> savedPlayers, Map<String, List<LeaderCard>> savedLeaderCards) {
        this.order = order;
        this.savedMarket = savedMarket;
        this.savedDevMarket = savedDevMarket;
        savedPlayers.forEach(elem -> elem.setLeaderCards(savedLeaderCards.get(elem.getNickname())));
        this.savedPlayers = savedPlayers.stream().map(Player::new).collect(Collectors.toList());
    }

    public static void load() {

        Gson gson = new Gson();

        RuntimeTypeAdapterFactory<SpecialAbility> shapeAdapterFactory = RuntimeTypeAdapterFactory
                .of(SpecialAbility.class, "abilityType")
                .registerSubtype(DiscountAbility.class, "discount")
                .registerSubtype(WhiteMarbleAbility.class, "marble")
                .registerSubtype(ExtraDepotAbility.class, "depot")
                .registerSubtype(ProductionAbility.class, "production");
        Gson gsonLeader = new GsonBuilder()
                .registerTypeAdapterFactory(shapeAdapterFactory)
                .create();

        try {

            PlayersOrder orderFile =  gson.fromJson(new FileReader(pathToSavedGame + "/order.json"), PlayersOrder.class);
            SavedResourceMarket marketFile =  gson.fromJson(new FileReader(pathToSavedGame + "/res_market.json"), SavedResourceMarket.class);
            SavedDevMarket devMarketFile =  gson.fromJson(new FileReader(pathToSavedGame + "/dev_market.json"), SavedDevMarket.class);
            List<PlayerView> players =  Stream.of(gson.fromJson(new FileReader(pathToSavedGame + "/players.json"), PlayerView[].class)).collect(Collectors.toList());
            List<SavedLeaderCards> leaderCardsFile =  Stream.of(gson.fromJson(new FileReader(pathToSavedGame + "/leader_cards.json"), SavedLeaderCards[].class)).collect(Collectors.toList());

            Map<String, List<LeaderCard>> playerLeaderMap = leaderCardsFile.stream().collect(Collectors.toMap(
                    SavedLeaderCards::getPlayer,
                    SavedLeaderCards::getLeaderCards
            ));

            Game.restore(new SavedState(orderFile, marketFile, devMarketFile, players, playerLeaderMap));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save(Game game) {

        Market market = game.getMarketInstance();
        DevMarket devMarket = game.getDevelopmentCardsMarket();
        Map<Player, List<MarketMarble>> marketMap;
        Map<Player, List<Resource>> devMap;

        try {
            marketMap = market.getAbilityMap();
        }
        catch(NotDecoratedException e) {
            marketMap = new HashMap<>();
        }

        try {
            devMap = devMarket.getMap();
        }
        catch(NotDecoratedException e) {
            devMap = new HashMap<>();
        }

        write(new SavedResourceMarket(market.getTray(), market.getExtra(), marketMap), "/res_market.json");
        write(new SavedDevMarket(devMarket.getDecks(), devMap), "/dev_market.json");
        write(new PlayersOrder(game.getPlayers(), game.getCurrentPlayer()), "/order.json");
        write(game.getPlayers().stream().map(PlayerView::new).collect(Collectors.toList()), "/players.json");
        write(game.getPlayers().stream().map(e -> new SavedLeaderCards(e.getNickname(), e.getLeaderCards())).collect(Collectors.toList()), "/leader_cards.json");

    }

    public static void write (Object source, String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileWriter writer;
        try {
            writer = new FileWriter(pathToSavedGame + path);
            gson.toJson(source, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayersOrder getOrder() {
        return order;
    }

    public SavedResourceMarket getSavedMarket() {
        return savedMarket;
    }

    public SavedDevMarket getSavedDevMarket() {
        return savedDevMarket;
    }

    public List<Player> getSavedPlayers() {
        return savedPlayers;
    }

    public Map<Player, List<MarketMarble>> getMarketMap() {
        return savedMarket.getSavedAbilityMap().entrySet().stream().collect(Collectors.toMap(
                entry -> getPlayerFromNick(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    public Map<Player, List<Resource>> getDevMap() {
        return savedDevMarket.getSavedAbilityMap().entrySet().stream().collect(Collectors.toMap(
                entry -> getPlayerFromNick(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    public int numOfPlayers() {
        return savedPlayers.size();
    }

    private Player getPlayerFromNick(String nickname) {
        for(Player p : savedPlayers) {
            if(p.getNickname().equals(nickname)) return p;
        }
        throw new RuntimeException("Player " + nickname + " does not exist");
    }

    public List<String> nicknameList() {
        return order.getOrder();
    }

    public Player currentPlayer() {
        return getPlayerFromNick(order.getCurrentPlayer());
    }
}
