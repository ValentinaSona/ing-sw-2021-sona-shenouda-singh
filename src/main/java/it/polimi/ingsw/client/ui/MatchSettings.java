package it.polimi.ingsw.client.ui;

import java.util.ArrayList;
import java.util.List;

public class MatchSettings {

    private int playersNum;
    private String clientNickname;
    private List<String> joiningPlayers;

    private static MatchSettings singleton;

    public static MatchSettings getInstance() {
        if (singleton == null) singleton = new MatchSettings();
        return singleton;
    }

    private MatchSettings(){
        joiningPlayers = new ArrayList<>();
    }

    public void setClientNickname(String clientNickname) {
        this.clientNickname = clientNickname;
    }

    public void setPlayersNum(int playersNum) {
        this.playersNum = playersNum;
    }

    public void addPlayer(String player) {
        if (joiningPlayers.size() < playersNum) joiningPlayers.add(player);
    }

    public int getPlayersNum() {
        return playersNum;
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public List<String> getJoiningPlayers() {
        return joiningPlayers;
    }
}
