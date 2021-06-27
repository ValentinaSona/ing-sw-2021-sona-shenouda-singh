package it.polimi.ingsw.client.ui.gui.JFXControllers;

public enum ScreenName {
    MAIN_MENU("mainScreen.fxml", new String[]{"mainText.css", "standardBackground.css"}),
    OPTIONS("optionsScreen.fxml", new String[]{"standardBackground.css", "buttons.css"}),
    SINGLEPLAYER("singleplayerScreen.fxml", new String[]{"standardBackground.css", "mainText.css", "buttons.css"}),
    CREDITS("creditsScreen.fxml", new String[]{"standardBackground.css", "mainText.css", "buttons.css"}),
    MULTIPLAYER("multiplayerScreen.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css"}),
    JOIN_GAME("joinScreen.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css"}),
    CREATION("gameCreation.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css"}),
    LOBBY("lobby.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css", "list.css"}),
    STARTING_CHOICE("startingChoice.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css", "gameTabs.css"}),
    PERSONAL_BOARD("personalBoardScreen.fxml", new String[]{"standardBackground.css", "gameTabs.css", "gameLog.css", "buttons.css", "scroll.css"}),
    MARKET("marketScreen.fxml", new String[]{"standardBackground.css", "gameTabs.css", "buttons.css", "gameLog.css"}),
    DEV_MARKET("devCardsMarket.fxml", new String[]{"standardBackground.css", "gameTabs.css", "buttons.css", "gameLog.css"}),
    OTHER_BOARD("otherBoard.fxml", new String[]{"standardBackground.css", "gameTabs.css", "gameLog.css", "buttons.css"}),
    LEADER("leaderCardsScreen.fxml", new String[]{"buttons.css"}),
    END_OF_GAME("endScreen.fxml", new String[]{"mainText.css", "standardBackground.css"}),
    JOIN_SINGLEPLAYER("joinSinglePlayer.fxml", new String[]{"mainText.css", "standardBackground.css", "buttons.css"}),
    GAME_MENU("gameMenu.fxml", new String[]{"buttons.css"}),
    WARNING("warning.fxml", new String[]{"mainText.css"});

    private String fxml;
    private String[] css;

    ScreenName(String fxml, String[] css) {
        this.fxml = fxml;
        this.css = css;
    }

    public String fxml() {
        return fxml;
    }

    public String[] css() {
        return css;
    }
}
