package it.polimi.ingsw.server.model;

public enum GameState {
        /*
        Waiting for the resources and LeaderCard choosen by the players
         */
        SETUP_GAME,
        /*
        Playing for real
         */
        PLAY,

        WAITING_FOR_SOMEONE,

        END_GAME;
}
