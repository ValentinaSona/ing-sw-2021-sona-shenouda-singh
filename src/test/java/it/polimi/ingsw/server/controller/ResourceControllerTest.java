package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.exception.DevelopmentCardException;
import it.polimi.ingsw.server.exception.EndOfGameException;
import it.polimi.ingsw.server.exception.InvalidDepotException;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.view.RealRemoteViewHandler;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientDepositIntoWarehouseMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientDepositResourceIntoSlotMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientTidyWarehouseMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ResourceControllerTest {
    Game model;
    ResourceController controller;


    @AfterEach
    void destroy(){
        model = null;
        controller = null;
        Game.destroy();
        ResourceController.destroy();
    }


    @Test
    void addFaithPoints() {

        model = Game.getInstance(2);
        controller = ResourceController.getInstance(model);

        User arthur = new User("Arthur");
        User merlin = new User("Merlin");

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        try {
            controller.addFaithPoints(model.getPlayerFromUser(arthur), new Resource(2, ResourceType.FAITH));
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(2, model.getPlayerFromUser(arthur).getFaithTrack().getFaithMarker());


        try {
            controller.addFaithPoints(model.getPlayerFromUser(merlin), new Resource(10, ResourceType.FAITH));
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(10, model.getPlayerFromUser(merlin).getFaithTrack().getFaithMarker());
        Assertions.assertEquals(2, model.getPlayerFromUser(arthur).getFaithTrack().getFaithMarker());

        Assertions.assertEquals(PopeFavorTiles.UPWARDS, model.getPlayerFromUser(merlin).getFaithTrack().getPopeFavorTiles(0));
        Assertions.assertEquals(PopeFavorTiles.DISMISSED, model.getPlayerFromUser(arthur).getFaithTrack().getPopeFavorTiles(0));


    }

    @Test
    void throwResources() {

        model = Game.getInstance(2);
        controller = ResourceController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");


        User arthur = new User("Arthur");
        User merlin = view.getUser();

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        model.getPlayerFromUser(merlin).toggleTurn();
        model.getPlayerFromUser(merlin).toggleMainAction();
        model.setGameState(GameState.PLAY);


        try {
            controller.throwResources(view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(0, model.getPlayerFromUser(merlin).getFaithTrack().getFaithMarker());
        Assertions.assertEquals(0, model.getPlayerFromUser(arthur).getFaithTrack().getFaithMarker());


        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(new Resource(2,ResourceType.STONE));
        model.getPlayerFromUser(merlin).addToTempResources(resources);

        try {
            controller.throwResources(view, merlin);
        } catch (EndOfGameException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(0, model.getPlayerFromUser(merlin).getFaithTrack().getFaithMarker());
        Assertions.assertEquals(2, model.getPlayerFromUser(arthur).getFaithTrack().getFaithMarker());



    }

    @Test
    void tidyWarehouse() {

        model = Game.getInstance(2);
        controller = ResourceController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");

        User arthur = new User("Arthur");
        User merlin = view.getUser();

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        model.getPlayerFromUser(merlin).toggleTurn();
        model.getPlayerFromUser(merlin).toggleMainAction();
        model.setGameState(GameState.PLAY);

        Depot targetDepot = model.getPlayerFromUser(merlin).getWarehouse().get(Id.DEPOT_2.getValue());

        try {
            targetDepot.addResource(new Resource(1, ResourceType.COIN));
        } catch (InvalidDepotException e) {
            Assertions.fail();
        }

        Assertions.assertEquals(new Resource(1, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());

        targetDepot = model.getPlayerFromUser(merlin).getWarehouse().get(Id.DEPOT_3.getValue());
        try {
            targetDepot.addResource(new Resource(2, ResourceType.SHIELD));
        } catch (InvalidDepotException e) {
            Assertions.fail();
        }

        Assertions.assertEquals(new Resource(2, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());


        var message = new ClientTidyWarehouseMessage(Id.DEPOT_3, Id.DEPOT_1);

        controller.tidyWarehouse(message,view, merlin);
        // Illegal move, no variation.
        Assertions.assertEquals(new Resource(1, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(2, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());


        message = new ClientTidyWarehouseMessage(Id.DEPOT_3, Id.DEPOT_2);

        controller.tidyWarehouse(message,view, merlin);

        Assertions.assertEquals(new Resource(2, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(1, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());

    }

    @Test
    void depositIntoWarehouse() {

        model = Game.getInstance(2);
        controller = ResourceController.getInstance(model);

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
        model.setGameState(GameState.PLAY);

        // Initialize tempResources.
        ArrayList<Resource> resources = new ArrayList<>();
        resources.add(new Resource(2,ResourceType.STONE));
        resources.add(new Resource(1,ResourceType.SHIELD));
        model.getPlayerFromUser(merlin).addToTempResources(resources);


        // One of the depots already contains something.
        Depot targetDepot = model.getPlayerFromUser(merlin).getWarehouse().get(Id.DEPOT_2.getValue());
        try {
            targetDepot.addResource(new Resource(1, ResourceType.SHIELD));
        } catch (InvalidDepotException e) {
            Assertions.fail();
        }

        Assertions.assertEquals(new Resource(1, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());


        var message = new ClientDepositIntoWarehouseMessage(Id.DEPOT_1, new Resource(2,ResourceType.STONE));
        controller.depositIntoWarehouse(message, view, merlin);
        // Illegal because of capacity.
        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(0).getResource());
        Assertions.assertEquals(new Resource(1, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());

        message = new ClientDepositIntoWarehouseMessage(Id.DEPOT_3, new Resource(2,ResourceType.STONE));
        controller.depositIntoWarehouse(message, view, merlin);

        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(0).getResource());
        Assertions.assertEquals(new Resource(1, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(2,ResourceType.STONE), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());

        message = new ClientDepositIntoWarehouseMessage(Id.DEPOT_1, new Resource(1,ResourceType.SHIELD));
        controller.depositIntoWarehouse(message, view, merlin);
        // Illegal because shields are already present in another depot.
        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(0).getResource());
        Assertions.assertEquals(new Resource(1, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(2,ResourceType.STONE), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());

        message = new ClientDepositIntoWarehouseMessage(Id.DEPOT_2, new Resource(1,ResourceType.SHIELD));
        controller.depositIntoWarehouse(message, view, merlin);
        // Legal
        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(0).getResource());
        Assertions.assertEquals(new Resource(2, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(2,ResourceType.STONE), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());


        message = new ClientDepositIntoWarehouseMessage(Id.DEPOT_1, new Resource(1,ResourceType.COIN));
        controller.depositIntoWarehouse(message, view, merlin);
        // Illegal as it is not part of temp Resources.
        assertNull(model.getPlayerFromUser(merlin).getWarehouse().get(0).getResource());
        Assertions.assertEquals(new Resource(2, ResourceType.SHIELD), model.getPlayerFromUser(merlin).getWarehouse().get(1).getResource());
        Assertions.assertEquals(new Resource(2,ResourceType.STONE), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());

        Assertions.assertEquals(new ArrayList<Resource>(), model.getPlayerFromUser(merlin).getTempResources());

    }

    @Test
    void depositResourceIntoSlot() {


        // D2 : 1 shield
        // D3 : 3 coin
        // B: 4 stones, 2 shield

        // Card cost: 1 coin


        model = Game.getInstance(2);
        controller = ResourceController.getInstance(model);

        Connection mockConnection = mock(Connection.class);
        RealRemoteViewHandler view = new RealRemoteViewHandler(mockConnection, "Merlin");

        User arthur = new User("Arthur");
        User merlin = view.getUser();

        model.subscribeUser(arthur);
        model.subscribeUser(merlin);

        model.getPlayerFromUser(merlin).toggleTurn();
        model.getPlayerFromUser(merlin).toggleMainAction();
        model.setGameState(GameState.PLAY);

        // Depot setup
        Depot targetDepot = model.getPlayerFromUser(merlin).getWarehouse().get(Id.DEPOT_2.getValue());
        try {
            targetDepot.addResource(new Resource(1, ResourceType.SHIELD));
        } catch (InvalidDepotException e) {
            Assertions.fail();
        }
        targetDepot = model.getPlayerFromUser(merlin).getWarehouse().get(Id.DEPOT_3.getValue());
        try {
            targetDepot.addResource(new Resource(3, ResourceType.COIN));
        } catch (InvalidDepotException e) {
            Assertions.fail();
        }

        // Strongbox setup
        model.getPlayerFromUser(merlin).getStrongbox().addResources(new Resource(4, ResourceType.STONE));
        model.getPlayerFromUser(merlin).getStrongbox().addResources(new Resource(2, ResourceType.SHIELD));

        // Target slot and card.
        var production = new Production(new Resource[]{new Resource(2, ResourceType.SHIELD)}, new Resource[]{new Resource(2,ResourceType.SERVANT)});
        var card = new DevelopmentCard(0, new Resource[]{new Resource(2,ResourceType.COIN)} , DevelopmentType.BLUE, 1, 1, production );
        var card2 = new DevelopmentCard(0, new Resource[]{new Resource(4,ResourceType.COIN), new Resource(4, ResourceType.STONE)} , DevelopmentType.BLUE, 1, 1, production );


        DevelopmentCardSlot targetSlot = model.getPlayerFromUser(merlin).getDevelopmentCardSlots()[0];
        try {
            targetSlot.setTargetCard(card, 1,2);
        } catch (DevelopmentCardException e) {
            Assertions.fail();
        }



        // Right number, wrong resource
        Map<Id, Resource> map = new HashMap<>();
        map.put(Id.DEPOT_3, new Resource(2,ResourceType.SHIELD));

        var message = new ClientDepositResourceIntoSlotMessage(Id.SLOT_1, map);
        controller.depositResourceIntoSlot(message, view, merlin);

        Assertions.assertEquals(new Resource(3, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());
        Assertions.assertFalse(targetSlot.isConfirmed());

        // Insufficient quantity, right resource
        map = new HashMap<>();
        map.put(Id.DEPOT_3, new Resource(1,ResourceType.COIN));
        map.put(Id.STRONGBOX_STONE, new Resource(2,ResourceType.STONE));

        message = new ClientDepositResourceIntoSlotMessage(Id.SLOT_1, map);
        controller.depositResourceIntoSlot(message, view, merlin);

        Assertions.assertEquals(new Resource(3, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());
        Assertions.assertEquals(new Resource(4, ResourceType.STONE), model.getPlayerFromUser(merlin).getStrongbox().getAvailableResources(ResourceType.STONE));
        Assertions.assertFalse(targetSlot.isConfirmed());

        /*
        try {
            targetSlot.setTargetCard(card2, 1,2);
        } catch (DevelopmentCardException e) {
            Assertions.fail();
        }


        // Missing one resource, financing from both depot and warehouse
        map = new HashMap<>();
        map.put(Id.DEPOT_3, new Resource(3,ResourceType.COIN));
        map.put(Id.STRONGBOX_COIN, new Resource(1,ResourceType.COIN));

        message = new ClientDepositResourceIntoSlotMessage(Id.SLOT_1, map);
        controller.depositResourceIntoSlot(message, view, merlin);

        Assertions.assertEquals(new Resource(3, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());
        Assertions.assertEquals(new Resource(4, ResourceType.STONE), model.getPlayerFromUser(merlin).getStrongbox().getAvailableResources(ResourceType.STONE));
        Assertions.assertFalse(targetSlot.isConfirmed());


        try {
            targetSlot.setTargetCard(card, 1,2);
        } catch (DevelopmentCardException e) {
            Assertions.fail();
        }

*/

        // Working
        map = new HashMap<>();
        map.put(Id.DEPOT_3, new Resource(2,ResourceType.COIN));

         message = new ClientDepositResourceIntoSlotMessage(Id.SLOT_1, map);
        controller.depositResourceIntoSlot(message, view, merlin);

        Assertions.assertEquals(new Resource(1, ResourceType.COIN), model.getPlayerFromUser(merlin).getWarehouse().get(2).getResource());
        Assertions.assertTrue(targetSlot.isConfirmed());





    }

    @Test
    void activateProduction() {
    }
}