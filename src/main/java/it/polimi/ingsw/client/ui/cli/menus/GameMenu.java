package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.DevelopmentCardSlotView;
import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Depot;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import javafx.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.CHECK_MARK;
import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;

public class GameMenu {

    private final CLI cli;
    private MenuRunner runner;



    public GameMenu(CLI cli) {
        this.cli = cli;
    }



    public void run(){
        this.runner = MenuRunner.getInstance(cli);
        gameMenu(false);
    }

    private String[] getGameOptions(){
        if (cli.getView().isMyTurn()){
            if (cli.getView().isMainAction()){

                return new String[] {"See market / buy marbles", "See development card market / buy cards ", "See your board / activate productions", "See other players boards", "See faith tracks",  "Arrange warehouse", "Leader Action", "End Turn"};
            } else {
                return new String[] {"See market", "See development card market", "See your board", "See other players boards",  "See faith tracks", "Arrange warehouse", "Leader Action", "End Turn"};
            }

        } else {
            return new String[] {"See market", "See development card market", "See your board", "See other players boards", "See faith tracks", "Arrange warehouse"};
        }
    }


    private void gameMenu(boolean hasBeenRefreshed) {
        String[] options = getGameOptions();

        switch (cli.getChoice(options, hasBeenRefreshed, true)){
            case 0 -> gameMenu(true);
            case 1 -> {
                marketBuy();
                gameMenu(false);
            }
            case 2 -> {
                cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());
                //cardBuy();
                gameMenu(false);
            }
            case 3 -> {
                runner.printDepots();
                runner.printPlayedLeaders();
                gameMenu(false);
            }

            case 5 -> {
                runner.printFaithTracks();
                gameMenu(false);
            }

            case 6 -> {
                tidyWarehouse();
                gameMenu(false);
            }
            case 7 -> {
                leaderAction();
                gameMenu(false);
            }
            case 8 -> {
                endOfTurn();
                gameMenu(false);
            }
            default -> gameMenu(false);
        }
    }

    private void cardBuy() {
        String[] options;
        cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());

        // Set the player's choice or return if they can only observe.
        if (cli.getView().isMyTurn() && cli.getView().isMainAction()) {
            if (!cli.getYesOrNo("Do you wish to buy a card?")) return;
        } else return;

        int[] choice = cli.getDevelopmentRowCol();

        //On top of which slot?
        options = new String[3];
        for (int i = 0 ; i <3; i++){
            options[i]=(i+1)+") \n"+((DevelopmentCardSlotView)cli.getView().getSlots().get(0)).peek().toString();
        }

        Id id = null;
        cli.printMessage("On top of which slot do you wish to place it?");
        switch (cli.getChoice(options)){
            case 1 -> id = Id.SLOT_1;
            case 2 -> id = Id.SLOT_2;
            case 3 -> id = Id.SLOT_3;
        }

        runner.setContextAction(GameActions.BUY_CARD);
        runner.setCurrentAction(GameActions.WAITING);
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().selectDevelopmentCard(choice[0],choice[1],id);
            runner.waitResponse();
        }
// TODO: return value of sendresponse = menu for clienterrors or requirements.
        if (runner.getCurrentAction()==GameActions.MENU) return;


        var card = GameView.getInstance().getDevelopmentCardsMarket().getTray()[choice[0]][choice[1]];
        Map<Id, Resource> map = new HashMap<>();

        boolean special = false;

        int special_num = 0;

        for (DepotView depot: cli.getView().getWarehouse()){
            if (depot.getId() == Id.S_DEPOT_1 || depot.getId() == Id.S_DEPOT_2 ) {
                special_num++;
                special = true;
            }
        }
        var cost = new ArrayList<Resource>(Arrays.asList(card.getCost()));
        while (!cost.isEmpty()) {
            boolean exit = false;
            runner.printDepots();
            //TODO print strongbox.
            cli.printMessage("[ ] Select the resources to pay and their source (e.g. 1 coin @ D1 - D for Depots, S for Special depots, B for strongBox)");
            Pair<Id, Resource> idResourcePair = cli.getIdResourcePair(true, special, special_num);
            int quantity = 0;
            switch (idResourcePair.getKey()){
                case STRONGBOX_COIN ->  quantity = cli.getView().getStrongboxView().getCoin().getQuantity();
                case STRONGBOX_SERVANT ->  quantity = cli.getView().getStrongboxView().getServant().getQuantity();
                case STRONGBOX_SHIELD ->  quantity = cli.getView().getStrongboxView().getShield().getQuantity();
                case STRONGBOX_STONE ->  quantity = cli.getView().getStrongboxView().getStone().getQuantity();
                case DEPOT_1 -> quantity = cli.getView().getWarehouse().get(0).getResource().getQuantity();
                case DEPOT_2 -> quantity = cli.getView().getWarehouse().get(1).getResource().getQuantity();
                case DEPOT_3 -> quantity = cli.getView().getWarehouse().get(2).getResource().getQuantity();
                case S_DEPOT_2 -> quantity = cli.getView().getWarehouse().get(4).getResource().getQuantity();
                case S_DEPOT_1 -> quantity = cli.getView().getWarehouse().get(3).getResource().getQuantity();
            }
            if (quantity<idResourcePair.getValue().getQuantity()){
                cli.printMessage("[X] The selected quantity is greater than what is contained in the source.");
                continue;
            }

            for (Resource res : cost){
                if (res.getResourceType()==idResourcePair.getValue().getResourceType()){
                    if (res.getQuantity()>=idResourcePair.getValue().getQuantity()){
                        res.sub(idResourcePair.getValue());
                        if(res.getQuantity()==0) cost.remove(res);
                    } else {
                        cli.printMessage("[X] The selected quantity is greater than needed to pay for the card.");
                        exit = true;
                    }
                    break;
                }
            }
            if (exit) continue;

            map.put(idResourcePair.getKey(), idResourcePair.getValue());

        }
        runner.setContextAction(GameActions.BUY_CARD);
        runner.setCurrentAction(GameActions.WAITING);
        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().depositResourcesIntoSlot(id, map);
            runner.waitResponse();

        }

    }


    private void leaderAction() {

        var cards = cli.getView().getLeaderCards();
        var inactiveCards = cards.stream().filter(leaderCard -> !leaderCard.isActive()).collect(Collectors.toList());

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
            chosen_index = cards.indexOf(inactiveCards.get(0));

        }

        if (chosen_index==1) {
            chosen = Id.LEADER_CARD_1;
        } else {
            chosen = Id.LEADER_CARD_2;
        }
        runner.setContextAction(GameActions.ACTIVATE_LEADER);
        runner.setCurrentAction(GameActions.WAITING);


        synchronized (MenuRunner.getInstance()) {
            if (choice == 1) {
                cli.printMessage("[ ] Activating :\n"+cards.get(chosen_index-1));
                UIController.getInstance().activateSpecialAbility(chosen);
            } else {
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
            // TODO: Two leaders
            depositResources();
        }
    }

    private void depositResources() {

        var warehouse = cli.getView().getWarehouse();
        int special_num =0;
        boolean special = false;

        String[] options = {"Deposit resource", "Rearrange warehouse", "Throw away remaining resources"};
        for (DepotView depot: warehouse){
            if (depot.getId() == Id.S_DEPOT_1 || depot.getId() == Id.S_DEPOT_2 ) {
                special_num++;
                special = true;
            }
        }

        var tempResources = cli.getView().getTempResources();

        while (!tempResources.isEmpty()){



            String resourcePrint = tempResources.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" , ", "", ""));


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
            Pair<Id, Resource> values = cli.getIdResourcePair(true, special, special_num);

            runner.setContextAction(GameActions.DEPOSIT_RESOURCES);
            runner.setCurrentAction(GameActions.WAITING);
            cli.printMessage("[ ] Depositing " + values.getKey() + " into " + values.getValue());

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
