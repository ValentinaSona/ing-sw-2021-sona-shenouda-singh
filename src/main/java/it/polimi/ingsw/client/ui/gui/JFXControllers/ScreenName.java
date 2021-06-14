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
    PERSONAL_BOARD("personalBoardScreen.fxml", new String[]{"standardBackground.css", "gameTabs.css"}),
    MARKET("marketScreen.fxml", new String[]{"standardBackground.css", "gameTabs.css", "buttons.css"});

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
