package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.LeaderCard;

import java.util.List;

public class SavedLeaderCards {

    private final String player;
    private final List<LeaderCard> leaderCards;

    public SavedLeaderCards(String player, List<LeaderCard> leaderCards) {
        this.player = player;
        this.leaderCards = leaderCards;
    }

    public String getPlayer() {
        return player;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }
}
