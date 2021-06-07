package it.polimi.ingsw.utils.persistence;

import it.polimi.ingsw.server.model.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class servers the purpose of storing in a file the order of the players in the game and the current player,
 * so that when a saved game is loaded the game can restart from the correct point
 */
public class PlayersOrder {

    private List<String> order;
    private String currentPlayer;

    public PlayersOrder (List<Player> players, Player currentPlayer) {

        order = players.stream().map(Player::getNickname).collect(Collectors.toList());
        this.currentPlayer = currentPlayer.getNickname();

    }

    public List<String> getOrder () {
        return order;
    }

    public String getCurrentPlayer () {
        return currentPlayer;
    }

}
