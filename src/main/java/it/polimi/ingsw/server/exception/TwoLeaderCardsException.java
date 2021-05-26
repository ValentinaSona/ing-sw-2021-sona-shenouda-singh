package it.polimi.ingsw.server.exception;

public class TwoLeaderCardsException extends Exception{
    private final Integer whiteMarbles;

    public TwoLeaderCardsException(Integer whiteMarbles){
        super();
        this.whiteMarbles = whiteMarbles;
    }

    public Integer getWhiteMarbles() {
        return whiteMarbles;
    }

}
