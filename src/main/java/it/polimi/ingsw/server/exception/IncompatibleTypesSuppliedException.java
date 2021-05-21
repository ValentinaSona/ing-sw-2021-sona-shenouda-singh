package it.polimi.ingsw.server.exception;

public class IncompatibleTypesSuppliedException extends Exception {
    private String reason;
    public IncompatibleTypesSuppliedException(String reason){
        super();
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}