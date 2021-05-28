package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientActivateSpecialAbilityMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertWhiteMarblesMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MarketControllerTest {
    Game model;
    MarketController controller;

    @AfterEach
    void destroy(){
        model = null;
        controller = null;
        Game.destroy();
        MarketController.destroy();
    }


    @Test
    void buyMarbles() {

        model = Game.getInstance(2);
        controller = MarketController.getInstance(model);

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

        Market market = model.getMarketInstance();

        var row0 = market.getTray()[0];
        ArrayList<Resource> resources = new ArrayList<>();
        Resource faithPoints = new Resource(0, ResourceType.FAITH);

        for(ResourceType type : ResourceType.values()){
            int count = 0;
            for(MarketMarble marble : row0){
                if(marble.convertToResource() == type)
                    count++;
            }
            if(count != 0)
                resources.add(new Resource(count, type));
        }


        //control if there is a faith resource
        for(Resource res : resources) {
            if (res.getResourceType() == ResourceType.FAITH) {
                faithPoints = res;
                resources.remove(res);
                break;
            }
        }

        var message = new ClientBuyMarblesMessage(0);

        controller.buyMarbles(message, view, merlin);

        Assertions.assertEquals(faithPoints.getQuantity(), model.getPlayerFromUser(merlin).getFaithTrack().getFaithMarker());
        Assertions.assertEquals(resources, model.getPlayerFromUser(merlin).getTempResources());



    }


    @Test
    void addMarketAbility() {
        model = Game.getInstance(2);
        controller = MarketController.getInstance(model);
        ResourceController con2 = ResourceController.getInstance(model);
        var resCon = ResourceController.getInstance(model);
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

        var marble1 = new WhiteMarbleAbility(MarketMarble.BLUE);
        var marble2 = new WhiteMarbleAbility(MarketMarble.GREY);
        List<MarketMarble> playerAbilities = new ArrayList<>();
        playerAbilities.add(MarketMarble.BLUE);
        playerAbilities.add(MarketMarble.GREY);


        controller.addMarketAbility(model.getPlayerFromUser(merlin), marble1 );
        controller.addMarketAbility(model.getPlayerFromUser(merlin), marble2 );

        Market market = model.getMarketInstance();

        Assertions.assertEquals(playerAbilities, market.getAbilityMap().get(model.getPlayerFromUser(merlin)));

        var tray = market.getTray();
        int i = 0;
        MarketMarble[] row0 = new MarketMarble[4];
        boolean white = false;
        int whites = 0;
        for (MarketMarble[] row : tray){
            int k = 0;
            for (MarketMarble marble : row){
                row0[k] = marble;
                if (marble.equals(MarketMarble.WHITE)) {
                    row0[k] = MarketMarble.BLUE;
                    white = true;
                    whites++;
                }
                k++;
            }
            if (white) break;
            else i++;
        }


        MarketMarble[] colors = new MarketMarble[whites];
        Arrays.fill(colors, MarketMarble.BLUE);

        ArrayList<Resource> resources = new ArrayList<>();
        Resource faithPoints = new Resource(0, ResourceType.FAITH);

        for(ResourceType type : ResourceType.values()){
            int count = 0;
            assert row0 != null;
            for(MarketMarble marble : row0){
                if(marble.convertToResource() == type)
                    count++;
            }
            if(count != 0)
                resources.add(new Resource(count, type));
        }


        //control if there is a faith resource
        for(Resource res : resources) {
            if (res.getResourceType() == ResourceType.FAITH) {
                faithPoints = res;
                resources.remove(res);
                break;
            }
        }


        var message = new ClientBuyMarblesMessage(i);
        controller.buyMarbles(message, view, merlin);

        var mess2 = new ClientConvertWhiteMarblesMessage(colors);

        controller.convertWhiteMarbles(mess2, view, merlin);

        var actual = model.getPlayerFromUser(merlin).getTempResources();
        for (int f = 0; f < resources.size(); f++){
            Assertions.assertEquals(resources.get(f),actual.get(f) );
        }

        con2 = null;
        MarketController.destroy();

    }
}