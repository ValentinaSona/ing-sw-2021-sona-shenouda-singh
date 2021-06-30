package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.modelview.DevelopmentCardSlotView;
import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.SpecialProductionView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.utils.GameActions;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerChooseWhiteMarblesMessage;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;
import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;

public class GameMenu {

    private final CLI cli;
    private MenuRunner runner;



    public GameMenu(CLI cli) {
        this.cli = cli;
    }

    public void setRunner(MenuRunner runner) {
        this.runner = runner;
    }

    public void run(){
        this.runner = MenuRunner.getInstance(cli);
        gameMenu();
    }

    private String[] getGameOptions() {
        if (cli.getView().isMyTurn()) {
            if (cli.getView().isMainAction()) {
                if (UIController.getInstance().isLocal()) {
                    return new String[]{"See market / buy marbles", "See development card market / buy cards ", "See your board / activate productions", "See other players boards", "See faith tracks", "Arrange warehouse", "Leader Action", "End Turn"};
                } else {
                    return new String[]{"See market / buy marbles", "See development card market / buy cards ", "See your board / activate productions", "See other players boards", "See faith tracks", "Arrange warehouse", "Leader Action", "End Turn", "Save game and quit"};
                }
            } else {
                if (UIController.getInstance().isLocal()) {
                    return new String[]{"See market", "See development card market", "See your board", "See other players boards", "See faith tracks", "Arrange warehouse", "Leader Action", "End Turn",};
                } else
                    return new String[]{"See market", "See development card market", "See your board", "See other players boards", "See faith tracks", "Arrange warehouse", "Leader Action", "End Turn", "Save game and quit"};
            }
        } else {
            return new String[]{"See market", "See development card market", "See your board", "See other players boards", "See faith tracks", "Arrange warehouse"};
        }
    }


    private void gameMenu() {
        boolean hasBeenRefreshed = false;

        do {
            String[] options = getGameOptions();

            switch (cli.getChoice(options, hasBeenRefreshed, true)) {
                case 0 -> hasBeenRefreshed = true;
                case 1 -> {
                    marketBuy();
                    hasBeenRefreshed = false;
                }
                case 2 -> {
                    cardBuy();
                    hasBeenRefreshed = false;
                }
                case 3 -> {
                    activateProductions();
                    hasBeenRefreshed = false;
                }
                case 4 -> {
                    runner.printBoards();
                    hasBeenRefreshed = false;
                }

                case 5 -> {
                    runner.printFaithTracks();
                    hasBeenRefreshed = false;
                }

                case 6 -> {
                    tidyWarehouse();
                    hasBeenRefreshed = false;
                }
                case 7 -> {
                    leaderAction();
                    hasBeenRefreshed = false;
                }
                case 8 -> {
                    endOfTurn();
                    hasBeenRefreshed = false;
                }
                case 9 -> {
                    UIController.getInstance().saveGame();
                    hasBeenRefreshed = false;
                }
                case 1492 -> {
                    UIController.getInstance().endGame();
                    hasBeenRefreshed = false;
                }
                default -> hasBeenRefreshed = false;
            }
        } while (runner.getState()== MenuStates.GAME);
    }

    private void activateProductions() {
        runner.printBoard();
        // Set the player's choice or return if they can only observe.
        if (cli.getView().isMyTurn() && cli.getView().isMainAction()) {
            if (!cli.getYesOrNo("Do you wish to activate your productions?")) return;
        } else return;

            Id[] slots = new Id[]{Id.BOARD_PRODUCTION, Id.SLOT_1, Id.SLOT_2, Id.SLOT_3, Id.S_SLOT_1, Id.S_SLOT_2};


        String[] options;
        // TODO better option print? include Prod description?

        if (cli.getView().getSlots().size() == 4) options = new String[]{"Board production","Slot 1", "Slot 2", "Slot 3", "Activate the selected productions"};
        else if (cli.getView().getSlots().size() == 5) options = new String[]{"Board production","Slot 1", "Slot 2", "Slot 3", "Special production 1", "Activate the selected productions"};
        else options = new String[]{"Board production","Slot 1", "Slot 2", "Slot 3", "Special production 1", "Special production 2", "Activate the selected productions"};


        int choice;
        int empty = 0;


        for (int i = 1; i < 4; i++){
          if(((DevelopmentCardSlotView)cli.getView().getSlots().get(i)).peek() == null){
              options[i] = options [i] + " - Empty";
              empty++;
              slots[i] = null;
          }
        }


        List<Id> productions = new ArrayList<>();
        Id prodId;
        Map<Id, Resource> resIdMap = new HashMap<>();
        Resource resource = null;
        // Loop start
        do {
            cli.printMessage("[ ] Choose the productions you want to activate : ");
             choice = cli.getChoice(options);

            // If the player is done selecting.
            if (choice == options.length) break;

            if(slots[choice-1]==null) {
                cli.printMessage("[X] This slot does not contain productions. ");
                continue;
            }


            prodId = null;
            if (!productions.contains(slots[choice-1])){
                productions.add(slots[choice-1]);
                prodId = slots[choice-1];
            } else {
                cli.printMessage("[X] Production already selected.");
                continue;
            }


            if (prodId == null) continue;

            resIdMap = new HashMap<>();
            resource = null;
            runner.printDepots();
            runner.printStrongbox();

            if (prodId == Id.BOARD_PRODUCTION){

                cli.printMessage("[ ] The board production converts two resources of your choice into one of your choice.");
                int costResources = 2;
                int possessedRes = 0;

                for (Resource res : cli.getView().getTotalResources()){
                    possessedRes += res.getQuantity();
                }

                if (possessedRes <2) {
                    cli.printMessage("[X] You do not have the resources needed to activate this production.");
                    continue;
                }

                while (costResources > 0 ){
                    // TODO: total selected  - print map.
                    cli.printMessage("[ ] Select the resources to spend ("+ costResources+" more to select):");
                    Pair<Id, Resource> values = cli.getIdResourcePair(true);

                    if(values.getValue().getQuantity() > costResources){
                        cli.printMessage("[X] More resources than needed have been selected");
                        continue;
                    }
                    if (!checkSourceContains(values, resIdMap,  new ArrayList<>(Arrays.asList(new Resource(costResources, ResourceType.JOLLY))))) continue;

                    if (!resIdMap.containsKey(values.getKey())){
                        resIdMap.put(values.getKey(), values.getValue());
                    } else {
                        resIdMap.get(values.getKey()).add(values.getValue());
                    }

                    costResources -= values.getValue().getQuantity();
                }

                cli.printMessage("[ ] Select the resources to obtain (1 to select):");
                resource = cli.getResource(1);

            }
            else if (prodId == Id.S_SLOT_1 || prodId == Id.S_SLOT_2 ){

                var special_production = (SpecialProductionView) cli.getView().getSlots().get(prodId.getValue());

                // Initialize cost creating new resources - this is to avoid modifying the card data.
                var cost = new ArrayList<Resource>();
                for (Resource res :special_production.getSpecialProduction().getProductionCost()){
                    cost.add(new Resource(res.getQuantity(), res.getResourceType()));
                }

                if (!cli.getView().canPay(special_production.getSpecialProduction().getProductionCost())) {
                    cli.printMessage("[X] You do not have the resources needed to activate this production.");
                    continue;
                }

                cli.printMessage("[ ] This special production converts "+ special_production.getSpecialProduction().getProductionCost()[0].toString() + " into a faith point and a resource of your choice.");
                cli.printMessage("[ ] Select the output resource:");
                resource = cli.getResource(1);
                Pair<Id, Resource> values;
                do {
                    cli.printMessage("[ ] Select the resources you will use to pay:");
                    values = cli.getIdResourcePair(true);

                    if (values.getValue().getQuantity() > 1) {
                        cli.printMessage("[X] More resources than needed have been selected");
                    }

                } while (!checkSourceContains(values, resIdMap, cost));

                resIdMap.put(values.getKey(), values.getValue());


            }
            else if (prodId == Id.SLOT_1 || prodId == Id.SLOT_2 || prodId == Id.SLOT_3 ) {
                var production = (DevelopmentCardSlotView) cli.getView().getSlots().get(prodId.getValue());

                // Safe init of list so that we can work on it without modifying the actual card - Important for local games.
                var cost = new ArrayList<Resource>();
                for (Resource res : production.peek().getProduction().getProductionCost()){
                    cost.add(new Resource(res.getQuantity(), res.getResourceType()));
                }

                if (!cli.getView().canPay(cost.toArray(Resource[]::new))) {
                    cli.printMessage("[X] You do not have the resources needed to activate this production.");
                    continue;
                }
                cli.printMessage("[ ] This  production converts "+ production.peek().getProduction().toString() + ".");

                // Only so not null fix later.
                resource = new Resource(1, ResourceType.JOLLY);
                while (!cost.isEmpty()) {
                    String costPrint = cost.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(" , ", "", ""));
                    cli.printMessage("[ ] Cost left to pay: " + costPrint);
                    cli.printMessage("[ ] Select the resources to pay and their source (e.g. 1 coin @ D1 - D for Depots, S for Special depots, B for strongBox)");
                    Pair<Id, Resource> idResourcePair = cli.getIdResourcePair(true);


                    if (!checkSourceContains(idResourcePair, resIdMap, cost)) continue;

                    if (resIdMap.containsKey(idResourcePair.getKey())) {
                        resIdMap.get(idResourcePair.getKey()).add(idResourcePair.getValue());
                    } else resIdMap.put(idResourcePair.getKey(), idResourcePair.getValue());

                }
            }

            assert resource != null;
            runner.setContextAction(GameActions.SELECT_PRODUCTION);
            runner.setCurrentAction(GameActions.WAITING);
            synchronized (MenuRunner.getInstance()) {

                UIController.getInstance().depositResourcesIntoSlot(prodId, resIdMap, resource.getResourceType(), false);
                runner.waitResponse();
            }

            if (runner.getCurrentAction() == GameActions.MENU){
                productions.remove(slots[choice-1]);
            } else {
                options[choice - 1] = options[choice - 1] + " - selected";
            }

            if (productions.size() == (options.length - empty - 1)) break;

        } while(true);


        if (productions.isEmpty()){return;}

        runner.setContextAction(GameActions.ACTIVATE_PRODUCTION);
        runner.setCurrentAction(GameActions.WAITING);
        synchronized (MenuRunner.getInstance()) {

            UIController.getInstance().activateProduction();
            runner.waitResponse();
        }

    }

    private void cardBuy() {
        String[] options;
        cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());

        // Check if there are discount abilities active
        var discounts = cli.getView().getLeaderCards().stream().filter(leaderCard -> leaderCard!=null && leaderCard.isActive() && leaderCard.getSpecialAbility() instanceof DiscountAbility).collect(Collectors.toList());

        // Inform the player
        for (LeaderCard discount : discounts){
            cli.printMessage("[" + CHECK_MARK + "] You have an extra " + ((DiscountAbility)discount.getSpecialAbility()).getDiscount()+ " discount on the prices above!");
        }

        // Set the player's choice or return if they can only observe.
        if (cli.getView().isMyTurn() && cli.getView().isMainAction()) {
            if (!cli.getYesOrNo("Do you wish to buy a card?")) return;
        } else return;

        // If player proceeds, get his choice of card
        int[] choice = cli.getDevelopmentRowCol();


        // IS THIS DISCOUNTED OR NOT? maybe it was the same other bug.
        var card = GameView.getInstance().getDevelopmentCardsMarket().getTray()[choice[0]][choice[1]];


        //On top of which slot?
        options = new String[3];
        for (int i = 0 ; i <3; i++){
            DevelopmentCard slotCard = ((DevelopmentCardSlotView)cli.getView().getSlots().get(i+1)).peek();
            options[i]=((slotCard!= null) ? "\n" + slotCard : "Empty slot");
        }

        Id id = null;

        cli.printMessage("[ ] On top of which slot do you wish to place it?");
        int chosen_id = cli.getChoice(options);
        switch (chosen_id){
            case 1 -> id = Id.SLOT_1;
            case 2 -> id = Id.SLOT_2;
            case 3 -> id = Id.SLOT_3;
        }

        runner.setContextAction(GameActions.SELECT_CARD);
        runner.setCurrentAction(GameActions.WAITING);
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().selectDevelopmentCard(choice[0],choice[1],id);
            runner.waitResponse();
        }

        if (runner.getCurrentAction()==GameActions.MENU) return;

        // Loop for resources.
        do {

            Map<Id, Resource> map = new HashMap<>();

            // Initialize cost creating new resources - this is to avoid modifying the card data.
            var cost = new ArrayList<Resource>();
            for (Resource res :card.getCost()){
                cost.add(new Resource(res.getQuantity(), res.getResourceType()));
            }
            for (LeaderCard discount: discounts){
                var sale = ((DiscountAbility)discount.getSpecialAbility()).getDiscount();
                for (Resource resource : cost){
                    if (resource.getResourceType() == sale.getResourceType()){
                        resource.sub(sale);
                        // This never happens with the original set of cards, but it's good practice to check.
                        if (resource.getQuantity()<=0) cost.remove(resource);
                        break;
                    }
                }
            }
            // Various print to deliver info to the player.
            cli.printMessage("[" + CHECK_MARK + "] Selected card: \n" + card);
            runner.printDepots();
            runner.printStrongbox();
            while (!cost.isEmpty()) {

                String costPrint = cost.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(", ", "", ""));


                cli.printMessage("[ ] Cost left to pay: " + costPrint);
                cli.printMessage("[ ] Select the resources to pay and their source (e.g. 1 coin @ D1 - D for Depots, S for Special depots, B for strongBox)");

                Pair<Id, Resource> idResourcePair = cli.getIdResourcePair(true);


                if (!checkSourceContains(idResourcePair, map, cost)) continue;

                if (map.containsKey(idResourcePair.getKey())) {
                    map.get(idResourcePair.getKey()).add(idResourcePair.getValue());
                } else map.put(idResourcePair.getKey(), idResourcePair.getValue());

            }

            runner.setContextAction(GameActions.BUY_CARD);
            runner.setCurrentAction(GameActions.WAITING);
            synchronized (MenuRunner.getInstance()) {
                UIController.getInstance().depositResourcesIntoSlot(id, map);
                runner.waitResponse();

            }

        // If this loops the server has not validated the idResourceMap
        } while(runner.getCurrentAction()==GameActions.MENU);



        runner.setContextAction(GameActions.ACQUIRE_CARD);
        runner.setCurrentAction(GameActions.WAITING);
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().buyTargetCard(id);
            runner.waitResponse();
        }



    }

    private boolean checkSourceContains( Pair<Id, Resource> idResourcePair, Map<Id, Resource> map,ArrayList<Resource> cost){
        Resource resource = null;
        // Gets quantity contained in each source.
        try {
            switch (idResourcePair.getKey()) {
                case STRONGBOX_COIN -> resource = cli.getView().getStrongboxView().getCoin();
                case STRONGBOX_SERVANT -> resource = cli.getView().getStrongboxView().getServant();
                case STRONGBOX_SHIELD -> resource = cli.getView().getStrongboxView().getShield();
                case STRONGBOX_STONE -> resource= cli.getView().getStrongboxView().getStone();
                case DEPOT_1 -> resource = cli.getView().getWarehouse().get(0).getResource();
                case DEPOT_2 -> resource = cli.getView().getWarehouse().get(1).getResource();
                case DEPOT_3 -> resource= cli.getView().getWarehouse().get(2).getResource();
                case S_DEPOT_2 -> resource = cli.getView().getWarehouse().get(4).getResource();
                case S_DEPOT_1 -> resource = cli.getView().getWarehouse().get(3).getResource();
            }
        } catch (NullPointerException e){
            resource = null;
        }
        // The quantity available in the depot
        int quantity;
        ResourceType type;
        if (resource!= null){
            quantity = resource.getQuantity();
            type = resource.getResourceType();
        } else {
            quantity = 0;
            type = null;
        }

        // If the player already extracted resources from there, subtract them.
        if (map.containsKey(idResourcePair.getKey())){
            quantity = quantity - idResourcePair.getValue().getQuantity();
        }

        // Check that they not taking more than available or of a different type.
        if (quantity<idResourcePair.getValue().getQuantity() || type== null || type!=idResourcePair.getValue().getResourceType()){
            cli.printMessage("[X] The selected quantity is greater than what is contained in the source.");
            return false;
        }



        // Update the cost
        for (Resource res : cost){
            if (res.getResourceType()==idResourcePair.getValue().getResourceType()){
                if (res.getQuantity()>=idResourcePair.getValue().getQuantity()) {
                    res.sub(idResourcePair.getValue());
                    if (res.getQuantity() == 0) cost.remove(res);
                    return true;
                } else {
                    cli.printMessage("[X] The selected quantity is greater than needed to pay for the card.");
                    return false;
                }
            } else if (res.getResourceType() == ResourceType.JOLLY){
                if (res.getQuantity()>=idResourcePair.getValue().getQuantity()) {
                    res.sub(new Resource(idResourcePair.getValue().getQuantity(), ResourceType.JOLLY));
                    if (res.getQuantity() == 0) cost.remove(res);
                    return true;
                } else {
                    cli.printMessage("[X] The selected quantity is greater than needed to pay for the card.");
                    return false;
                }
            }
        }
        cli.printMessage("[X] The selected resource is not needed to pay for the card.");
        return false;
    }

    private void leaderAction() {

        var cards = cli.getView().getLeaderCards();
        var inactiveCards = cards.stream().filter(leaderCard -> leaderCard!=null && !leaderCard.isActive()).collect(Collectors.toList());

        if (inactiveCards.size() != 0)
            cli.printMessage("These are the leader cards still in your hand:");

        runner.printHand();
        if (inactiveCards.size() == 0) return;

        String[] options = {"Activate leader card", "Throw away leader card", "Back to menu"};

        int choice = cli.getChoice(options);

        if (choice == 3) return;

        Id chosen;
        int chosen_index;
        if (inactiveCards.size()==2) {
            cli.printMessage("[ ] Which card? (1 or 2)");
            chosen_index = cli.getInt(1, 2);

        } else {

            chosen_index = 1 + cards.indexOf(inactiveCards.get(0));

        }

        if (chosen_index==1) {
            chosen = Id.LEADER_CARD_1;
        } else {
            chosen = Id.LEADER_CARD_2;
        }

        runner.setCurrentAction(GameActions.WAITING);


        synchronized (MenuRunner.getInstance()) {
            if (choice == 1) {
                runner.setContextAction(GameActions.ACTIVATE_LEADER);
                cli.printMessage("[ ] Activating :\n"+cards.get(chosen_index-1));
                UIController.getInstance().activateSpecialAbility(chosen);
            } else {
                runner.setContextAction(GameActions.THROW_LEADER);
                cli.printMessage("[ ] Throwing away :\n"+cards.get(chosen_index-1));
                UIController.getInstance().throwLeaderCard(chosen);
            }
            runner.waitResponse();
        }



    }

    private void endOfTurn(){

        MenuRunner.getInstance().setContextAction(GameActions.END_TURN);
        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);

        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().endTurn();
            runner.waitResponse();
        }
    }

    private void marketBuy() {
        String[] options;
        cli.printMessage(MARBLE_LEGEND);
        cli.printMessage(GameView.getInstance().getMarketInstance());

        // Check if there are discount abilities active
        var discounts = cli.getView().getLeaderCards().stream().filter(leaderCard -> leaderCard!=null && leaderCard.isActive() && leaderCard.getSpecialAbility() instanceof WhiteMarbleAbility).collect(Collectors.toList());

        // Inform the player
        for (LeaderCard discount : discounts){
            cli.printMessage("[" + CHECK_MARK + "] Your white marbles can be converted into " + ((WhiteMarbleAbility)discount.getSpecialAbility()).getMarble()+ " marbles!");
        }

        // Set the player's choice or return if they can only observe.
        if (cli.getView().isMyTurn() && cli.getView().isMainAction()){
            options = new String[]{"Row 1", "Row 2", "Row 3", "Column 1", "Column 2", "Column 3", "Column 4", "No, Return to game menu"};
        } else return;

        // Get the choice.
        cli.printMessage("[ ] Do you wish to take resources from the market?");
        int choice = cli.getChoice(options);

        // Stop if they don't wish to buy.
        if (choice == options.length) return;

        cli.printMessage("[ ] Buying marbles from "+ options[choice-1]);

        runner.setContextAction(GameActions.BUY_MARBLES);
        runner.setCurrentAction(GameActions.WAITING);

        // Send server the buy message and await the brought message or the 2 leader cards one.
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().buyMarbles(choice-1);
            runner.waitResponse();
        }

        if (runner.getCurrentAction() == GameActions.DEPOSIT_RESOURCES){
            depositResources();
        } else if (runner.getCurrentAction() == GameActions.TWO_LEADERS){
            twoLeaders();
            depositResources();
        }
    }


    private void twoLeaders() {

        int marbles = ((ServerChooseWhiteMarblesMessage)runner.getInMsg()).getWhiteMarbles();
        cli.printMessage("[ ] You have bought "+ marbles +" white marbles.");
        MarketMarble[] choices = new MarketMarble[marbles];

        var abilities = cli.getView().getLeaderCards().stream()
                .filter(leaderCard -> leaderCard!=null && leaderCard.isActive() && leaderCard.getSpecialAbility() instanceof WhiteMarbleAbility)
                .map(LeaderCard::getSpecialAbility)
                .map(WhiteMarbleAbility.class::cast)
                .map(WhiteMarbleAbility::getMarble)
                .collect(Collectors.toList());

        cli.printMessage("Choose between 1) "+ abilities.get(0)+ " marble and 2) "+ abilities.get(1)+" marble.");
        for (int i = 0; i < marbles; i++) {
            cli.printMessage("[ ] Which color do you wish to convert marble #"+(i+1)+" to?");
            int colorChoice = cli.getInt(1,2);
            choices[i] = abilities.get(colorChoice-1);
        }

        runner.setContextAction(GameActions.BUY_MARBLES);
        runner.setCurrentAction(GameActions.WAITING);

        // Send server the buy message and await the brought message or the 2 leader cards one.
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().convertWhiteMarbles(choices);
            runner.waitResponse();
        }
    }

    public void depositResources() {

        String[] options = {"Deposit resource", "Rearrange warehouse", "Throw away remaining resources"};

        var tempResources = cli.getView().getTempResources();

        while (!tempResources.isEmpty()){



            String resourcePrint = tempResources.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ", "", ""));


            cli.printMessage("["+CHECK_MARK+"] You have received the following resources: "+ resourcePrint);
            cli.printMessage("["+CHECK_MARK+"] This is the state of your depots: ");
            runner.printDepots();

            int choice = cli.getChoice(options);

            if (choice == 2 ) {
                tidyWarehouse();
                continue;
            }

            if (choice == 3){
                UIController.getInstance().throwResources();
                return;
            }


            cli.printMessage("[ ] Select the resources and the depot you wish to deposit into (e.g. 1 coin @ D1 or 2 servants @ S1 for special depots)");
            Pair<Id, Resource> values = cli.getIdResourcePair(false);

            runner.setContextAction(GameActions.DEPOSIT_RESOURCES);
            runner.setCurrentAction(GameActions.WAITING);
            cli.printMessage("[ ] Depositing " + values.getValue()+ " into " + values.getKey());

            synchronized (MenuRunner.getInstance()) {
                UIController.getInstance().depositIntoWarehouse(values.getKey(), values.getValue());
                runner.waitResponse();
            }



            tempResources = cli.getView().getTempResources();
        }
    }

    private void tidyWarehouse(){
        runner.printDepots();

        String[] options;

        Id[] depots = new Id[]{Id.DEPOT_1, Id.DEPOT_2, Id.DEPOT_3, Id.S_DEPOT_1, Id.S_DEPOT_2};

        if (cli.getView().getWarehouse().size() == 3) options = new String[]{"Depot 1", "Depot 2", "Depot 3", "Cancel operation"};
        else if (cli.getView().getWarehouse().size() == 4) options = new String[]{"Depot 1", "Depot 2", "Depot 3","Special depot 1", "Cancel operation"};
        else options = new String[]{"Depot 1", "Depot 2", "Depot 3","Special depot 1", "Special depot 2", "Cancel operation"};

        cli.printMessage("[ ] Choose the depots you want to switch the content of: (1/2)");
        int choice = cli.getChoice(options);

        // If the player has chosen not to rearrange their warehouse
        if (choice == options.length) return;


        // Mark the already selected depot
        String unselected = new String(options[choice-1]);
        options[choice-1] = options[choice-1] + " - selected";

        cli.printMessage("[ ] Choose the depots you want to switch the content of: (1/2)");
        int choice2;
        do {
            choice2 = cli.getChoice(options);
        } while (choice == choice2);

        // If the player has chosen again not to rearrange their warehouse
        if (choice2 == options.length) return;

        options[choice-1] = unselected;


        runner.setContextAction(GameActions.TIDY_WAREHOUSE);
        runner.setCurrentAction(GameActions.WAITING);

        cli.printMessage("[ ] Swapping "+ options[choice-1]+" and "+ options[choice2-1]);

        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().tidyWarehouse(depots[choice - 1], depots[choice2 - 1]);
            runner.waitResponse();
        }

        if (cli.getView().getTempResources()!= null)
            runner.setContextAction(GameActions.DEPOSIT_RESOURCES);
    }

}
