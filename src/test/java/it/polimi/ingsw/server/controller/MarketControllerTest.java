package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.NotDecoratedException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientBuyMarblesMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientConvertWhiteMarblesMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        ResourceController.destroy();
    }


    @Test
    void buyMarbles() {

        model = Game.getInstance(2);
        controller = MarketController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");

        Connection mockConnection2 = mock(Connection.class);
        RealRemoteViewHandler view2 = new RealRemoteViewHandler(mockConnection2, "Arthur");


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

        try {
            controller.buyMarbles(message, view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }

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
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");

        Connection mockConnection2 = mock(Connection.class);
        RealRemoteViewHandler view2 = new RealRemoteViewHandler(mockConnection2, "Arthur");


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

        try {
            Assertions.assertEquals(playerAbilities, market.getAbilityMap().get(model.getPlayerFromUser(merlin)));
        } catch (NotDecoratedException e) {
            e.printStackTrace();
        }

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
        try {
            controller.buyMarbles(message, view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }

        var mess2 = new ClientConvertWhiteMarblesMessage(colors);

        try {
            controller.convertWhiteMarbles(mess2, view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }

        var actual = model.getPlayerFromUser(merlin).getTempResources();
        for (int f = 0; f < resources.size(); f++){
            Assertions.assertEquals(resources.get(f),actual.get(f) );
        }

        con2 = null;
        resCon = null;
        ResourceController.destroy();
        MarketController.destroy();

    }


}