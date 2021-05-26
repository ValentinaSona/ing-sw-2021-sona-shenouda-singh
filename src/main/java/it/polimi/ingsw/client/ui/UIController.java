package it.polimi.ingsw.client.ui;

/**
 * This should be a universal controller called by the ui
 */

public class UIController {

    private static UIController singleton;

    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    private UIController() {};

    public boolean sendNickname(String nickname) {
        MatchSettings.getInstance().setClientNickname(nickname);
        return true;
    }

    public boolean joinLobby() {
        return true;
    }

    public boolean setCreation(int playersNum) {
        MatchSettings.getInstance().setPlayersNum(playersNum);
        MatchSettings.getInstance().addPlayer(MatchSettings.getInstance().getClientNickname());
        return true;
    }

    public String getClientNickname() {
        return  MatchSettings.getInstance().getClientNickname();
    }

    public int currentPlayerNum() {
        return MatchSettings.getInstance().getJoiningPlayers().size();
    }

    public int totalPlayerNum() {
        return MatchSettings.getInstance().getPlayersNum();
    }
}
