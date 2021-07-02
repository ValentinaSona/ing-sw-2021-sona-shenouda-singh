package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.GameState;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientEndTurnMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

class TurnControllerTest {
    Game model;
    TurnController controller;

    @AfterEach
    void destroy(){
        model = null;
        controller = null;
        Game.destroy();
        TurnController.destroy();
        ResourceController.destroy();
    }

    @Test
    void endTurn() {

        model = Game.getInstance(2);
        controller = TurnController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");

        Connection mockConnection2 = mock(Connection.class);
        RealRemoteViewHandler view2 = new RealRemoteViewHandler(mockConnection2, "Arthur");


        User arthur = view2.getUser();
        User merlin = view.getUser();

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        model.getPlayerFromUser(merlin).toggleTurn();
        model.setCurrentPlayer(model.getPlayerFromUser(merlin));
        model.setGameState(GameState.PLAY);

        try {
            controller.endTurn(new ClientEndTurnMessage(), view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(model.getUserFromPlayer(model.getCurrentPlayer()), arthur);
        Assertions.assertTrue(model.getPlayerFromUser(arthur).getTurn());
        Assertions.assertTrue(model.getPlayerFromUser(arthur).getMainAction());
        Assertions.assertFalse(model.getPlayerFromUser(merlin).getTurn());
        Assertions.assertFalse(model.getPlayerFromUser(merlin).getMainAction());

        model.getPlayerFromUser(arthur).toggleMainAction();
        try {
            controller.endTurn(new ClientEndTurnMessage(), view, arthur);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(model.getUserFromPlayer(model.getCurrentPlayer()), merlin);
        Assertions.assertFalse(model.getPlayerFromUser(arthur).getTurn());
        Assertions.assertFalse(model.getPlayerFromUser(arthur).getMainAction());
        Assertions.assertTrue(model.getPlayerFromUser(merlin).getTurn());
        Assertions.assertTrue(model.getPlayerFromUser(merlin).getMainAction());

    }
}