package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.LeaderCard;

import java.util.List;

/**
 * Contains an instance of all the leader cards and their corresponding player
 */
public class PlayerLeaderCards {

    private final String player;
    private final List<LeaderCard> leaderCards;

    public PlayerLeaderCards(String player, List<LeaderCard> leaderCards) {
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