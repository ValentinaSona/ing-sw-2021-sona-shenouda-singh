package it.polimi.ingsw.server.exception;

import it.polimi.ingsw.server.controller.User;

public class EndOfGameException extends Exception {
    private final User user;
    private final EndOfGameCause cause;

    public EndOfGameException(EndOfGameCause cause, User user) {
        this.cause = cause;
        this.user = user;
    }

    public EndOfGameCause getEndCause() {
        return cause;
    }

    public User getUser() {
        return user;
    }
}