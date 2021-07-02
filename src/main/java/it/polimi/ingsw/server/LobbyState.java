package it.polimi.ingsw.server;

public enum LobbyState {
    /**
     * Lobby is the setup state and it is waiting for users to start/load a game
     */
    LOBBY_SETUP,
    /**
     * Lobby is doing all the setup needed to start/load a game
     */
    GAME_SETUP,
    /**
     * Lobby is loading a game from file
     */
    LOAD_GAME,
    /**
     * The game is started successfully and now the lobby is waiting for all the possible players
     * that could disconnect from an ongoing game
     */
    IN_GAME
}
