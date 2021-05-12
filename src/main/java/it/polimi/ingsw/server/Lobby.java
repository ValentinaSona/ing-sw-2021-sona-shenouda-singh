package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {
    private static Lobby singleton;
    private final Server server;
    private final Map<Connection, String> registeredNicknamesMap;
    //ordered list that contains player who requested to be inserted in the lobby
    private final LinkedList<Connection>  requestingConnections;
    //lock used to synchronize over the currentLobbyPlayerCount
    private final Object playerCountLock;
    //the first connection to arrive that has the control on the player count
    private Connection firstConnection;
    //maximum number of player
    private int currentLobbyPlayerCount;

    public static Lobby getInstance(Server server){
        if(singleton  == null){
            singleton = new Lobby(server);
        }
        return singleton;
    }

    private Lobby(Server server){
        this.server = server;
        this.registeredNicknamesMap = new ConcurrentHashMap<>();
        this.requestingConnections = new LinkedList<>();
        this.playerCountLock = new Object();
    }

    public boolean registerNickname(String nickname, Connection connection){
        //while i player is trying to register the map can't be modified by other threads
        synchronized (registeredNicknamesMap) {
            if (registeredNicknamesMap.containsValue(nickname) || registeredNicknamesMap.containsKey(connection)) {
                //we have already a player with that name or a connection from that client
                return false;
            } else {
                registeredNicknamesMap.put(connection, nickname);
                return true;
            }
        }
    }

    public void removeNickname(String nickname){
        synchronized (registeredNicknamesMap){
            registeredNicknamesMap.entrySet().stream()
                    .filter(entry-> entry.getValue().equals(nickname))
                    .forEach(entry -> registeredNicknamesMap.remove(entry.getKey(), entry.getValue()));
        }
    }


}
