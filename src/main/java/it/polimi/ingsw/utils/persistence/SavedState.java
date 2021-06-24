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
    private static boolean flag = false;
    private static final String pathToSavedGame = "./saved_game"; //TODO IMPORTANTE!!! DA DEFINIRE BENE ALTRIMENTI NON FUNZIONA NULLA

    private final PlayersOrder order;
    private final SavedResourceMarket savedMarket;
    private final SavedDevMarket savedDevMarket;
    private final List<Player> savedPlayers;

    private static final RuntimeTypeAdapterFactory<SpecialAbility> leaderAdapterFactory = RuntimeTypeAdapterFactory
            .of(SpecialAbility.class, "SpecialAbilityType")
            .registerSubtype(DiscountAbility.class, "discount")
            .registerSubtype(WhiteMarbleAbility.class, "marble")
            .registerSubtype(ExtraDepotAbility.class, "depot")
            .registerSubtype(ProductionAbility.class, "production");

    private static final RuntimeTypeAdapterFactory<Slot> slotAdapterFactory = RuntimeTypeAdapterFactory
            .of(Slot.class, "specialSlotType")
            .registerSubtype(DevelopmentCardSlot.class, "SLOT")
            .registerSubtype(BoardProduction.class, "BOARD_PRODUCTION")
            .registerSubtype(SpecialProduction.class, "S_SLOT");

    private static final Gson gson= new GsonBuilder()
            .registerTypeAdapterFactory(slotAdapterFactory)
            .registerTypeAdapterFactory(leaderAdapterFactory)
            .setPrettyPrinting()
            .create();



    public SavedState(PlayersOrder order, SavedResourceMarket savedMarket, SavedDevMarket savedDevMarket, List<PlayerView> savedPlayers, Map<String, List<LeaderCard>> savedLeaderCards, Map<String,List<Slot>> savedSlot) {
        this.order = order;
        this.savedMarket = savedMarket;
        this.savedDevMarket = savedDevMarket;
        savedPlayers.forEach(elem -> elem.setLeaderCards(savedLeaderCards.get(elem.getNickname())));

        this.savedPlayers = savedPlayers.stream().map(e -> new Player(e, savedSlot.get(e.getNickname()))).collect(Collectors.toList());
    }

    public static void load() {


        try {

            PlayersOrder orderFile =  gson.fromJson(new FileReader(pathToSavedGame + "/order.json"), PlayersOrder.class);
            SavedResourceMarket marketFile =  gson.fromJson(new FileReader(pathToSavedGame + "/res_market.json"), SavedResourceMarket.class);
            SavedDevMarket devMarketFile =  gson.fromJson(new FileReader(pathToSavedGame + "/dev_market.json"), SavedDevMarket.class);
            List<PlayerView> players =  Stream.of(gson.fromJson(new FileReader(pathToSavedGame + "/players.json"), PlayerView[].class)).collect(Collectors.toList());
            List<PlayerLeaderCards> leaderCardsFile =  Stream.of(gson.fromJson(new FileReader(pathToSavedGame + "/leader_cards.json"), PlayerLeaderCards[].class)).collect(Collectors.toList());
            List<PlayerSlot> slotFile = Stream.of(gson.fromJson(new FileReader(pathToSavedGame + "/slots.json"), PlayerSlot[].class)).collect(Collectors.toList());

            Map<String, List<LeaderCard>> playerLeaderMap = leaderCardsFile.stream().collect(Collectors.toMap(
                    PlayerLeaderCards::getPlayer,
                    PlayerLeaderCards::getLeaderCards
            ));

            Map<String, List<Slot>> playerSlotMap = slotFile.stream().collect(Collectors.toMap(
                    PlayerSlot::getNickname,
                    PlayerSlot::getSlots
            ));

            Game.restore(new SavedState(orderFile, marketFile, devMarketFile, players, playerLeaderMap, playerSlotMap));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            flag = false;
        }
    }

    public static void save(Game game) {
        flag = true;
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
        write(game.getPlayers().stream().map(e -> new PlayerLeaderCards(e.getNickname(), e.getLeaderCards())).collect(Collectors.toList()), "/leader_cards.json");
        write(game.getPlayers().stream().map(PlayerSlot::new).collect(Collectors.toList()), "/slots.json");

    }

    public static void write (Object source, String path) {



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
