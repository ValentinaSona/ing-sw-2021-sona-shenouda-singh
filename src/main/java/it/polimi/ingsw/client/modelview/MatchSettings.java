package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.controller.User;

import java.util.List;

public class MatchSettings {

    private boolean solo;

    private int totalUsers;
    private String clientNickname;
    private List<User> joiningPlayers;

    private static MatchSettings singleton;

    public static MatchSettings getInstance() {
        if (singleton == null) singleton = new MatchSettings();
        return singleton;
    }

    private MatchSettings(){
        solo = false;
    }

    public void setClientNickname(String clientNickname) {
        this.clientNickname = clientNickname;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public int getCurrentUsersNum(){

        if(joiningPlayers == null){
            return 1;
        }else{
            return joiningPlayers.size();
        }
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public void setJoiningUsers(List<User> users){
        joiningPlayers = users;
    }

    public List<User> getJoiningUsers() {
        return joiningPlayers;
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
    }

    public boolean isSolo() {
        return solo;
    }
}
