package it.polimi.ingsw.server.exception;

public class EndOfGameException extends Exception {
    private final EndOfGameCause cause;

    public EndOfGameException(EndOfGameCause cause) {
        this.cause = cause;
    }

    public EndOfGameCause getEndCause() {
        return cause;
    }
}