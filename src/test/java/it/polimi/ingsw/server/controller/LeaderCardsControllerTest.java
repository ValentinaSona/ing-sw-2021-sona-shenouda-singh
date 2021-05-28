package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientActivateSpecialAbilityMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientThrowLeaderCardMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LeaderCardsControllerTest {
    Game model;
    LeaderCardsController controller;
    @Test
    void activateSpecialAbility() {
        model = Game.getInstance(2);
        controller = LeaderCardsController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RemoteViewHandler view = new RemoteViewHandler(mockConnection, "Merlin");

        Connection mockConnection2 = mock(Connection.class);
        RemoteViewHandler view2 = new RemoteViewHandler(mockConnection2, "Arthur");

        User arthur = view2.getUser();
        User merlin = view.getUser();

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        model.getPlayerFromUser(merlin).toggleTurn();
        model.getPlayerFromUser(merlin).toggleMainAction();
        model.setCurrentPlayer(model.getPlayerFromUser(merlin));
        model.setGameState(GameState.PLAY);

        var req = new Requirement[]{new Requirement(1,1,DevelopmentType.BLUE)};
        var req2 = new Requirement[]{new Requirement(new Resource(2, ResourceType.COIN))};
        LeaderCard card = new LeaderCard(0,req,2,new WhiteMarbleAbility(MarketMarble.BLUE));
        LeaderCard card2 = new LeaderCard(0,req2,2,new DiscountAbility(new Resource(1,ResourceType.SHIELD)));

        model.getPlayerFromUser(merlin).setLeaderCards(new LeaderCard[]{card, card2});
        model.getPlayerFromUser(merlin).getStrongbox().addResources(new Resource(3, ResourceType.COIN));

        var message = new ClientActivateSpecialAbilityMessage(Id.LEADER_CARD_1);
        controller.activateSpecialAbility(message,view,merlin);

        Assertions.assertFalse(model.getPlayerFromUser(merlin).getLeaderCards().get(0).isActive());
        Assertions.assertFalse(model.getPlayerFromUser(merlin).getLeaderCards().get(1).isActive());

        message = new ClientActivateSpecialAbilityMessage(Id.LEADER_CARD_2);
        controller.activateSpecialAbility(message,view,merlin);

        Assertions.assertFalse(model.getPlayerFromUser(merlin).getLeaderCards().get(0).isActive());
        Assertions.assertTrue(model.getPlayerFromUser(merlin).getLeaderCards().get(1).isActive());


        var message2 = new ClientThrowLeaderCardMessage(Id.LEADER_CARD_1);
        controller.throwLeaderCard(message2,view, merlin);
        Assertions.assertNull(model.getPlayerFromUser(merlin).getLeaderCards().get(Id.LEADER_CARD_1.getValue()));

    }

    @Test
    void throwLeaderCard() {
    }
}