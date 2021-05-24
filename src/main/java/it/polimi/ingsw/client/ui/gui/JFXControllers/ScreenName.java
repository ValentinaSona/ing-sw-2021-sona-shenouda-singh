package it.polimi.ingsw.client.ui.gui.JFXControllers;

public enum ScreenName {
    MAIN_MENU("mainScreen.fxml", "style1.css"),
    MULTIPLAYER("multiplayerScreen.fxml", "style1.css"),
    JOIN_GAME("joinScreen.fxml", "style1.css"),
    LOBBY("lobby.fxml", "style1.css");

    private String fxml;
    private String css;

    ScreenName(String fxml, String css) {
        this.fxml = fxml;
        this.css = css;
    }

    public String fxml() {
        return fxml;
    }

    public String css() {
        return css;
    }
}
