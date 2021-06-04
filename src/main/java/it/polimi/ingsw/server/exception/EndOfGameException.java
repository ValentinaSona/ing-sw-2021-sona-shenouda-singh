package it.polimi.ingsw.server.exception;

public class EndOfGameException extends Exception {
    private final boolean lost;

    public EndOfGameException(boolean lost) {
        this.lost = lost;
    }


    public boolean hasLost() {
        return lost;
    }}