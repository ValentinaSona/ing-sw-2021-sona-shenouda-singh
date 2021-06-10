package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

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
        gameMenu();
    }

    private String[] getGameOptions(){
        if (cli.getView().isMyTurn()){
            if (cli.getView().isMainAction()){

                return new String[] {"See market", "See development card market", "See faith tracks", "See your board", "See other players boards", "Arrange warehouse", "Buy from market", "Buy Development Card", "Activate Productions", "Leader Action", "End Turn"};
            } else {
                return new String[] {"See market", "See development card market", "See faith tracks", "See your board", "See other players boards", "Arrange warehouse", "Leader Action", "End Turn"};
            }

        } else {
            return new String[] {"See market", "See development card market", "See faith tracks", "See your board", "See other players boards", "Arrange warehouse"};
        }
    }


    private void gameMenu() {
        String[] options =
                {"See market", "See development card market", "See faith tracks", "See your board", "See other players boards", "Arrange warehouse", "Buy from market", "Buy Development Card", "Activate Productions", "Leader Action", "End Turn"};

        switch (cli.getChoice(options)){
            case 1 -> {
                cli.printMessage(MARBLE_LEGEND);
                cli.printMessage(GameView.getInstance().getMarketInstance());
                gameMenu();
            }
            case 2 -> {
                cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());
                gameMenu();
            }
            case 3 -> {
                runner.printFaithTracks();
                gameMenu();
            }
            case 4 -> {
                runner.printDepots();
                runner.printPlayedLeaders();
                gameMenu();
            }
            case 6 -> {
                tidyWarehouse();
                gameMenu();
            }
            case 10 -> {
                runner.printHand();
                gameMenu();
            }
            default -> gameMenu();
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


        runner.setLastAction(GameActions.TIDY_WAREHOUSE);
        runner.setPresentAction(GameActions.WAITING);

        cli.printMessage("[ ] Swapping "+ options[choice-1]+" and "+ options[choice2-1]);

        synchronized (MenuRunner.getInstance()) {
            UIController.getInstance().tidyWarehouse(depots[choice - 1], depots[choice2 - 1]);
            runner.waitResponse();
        }

        runner.setPresentAction(GameActions.MENU);
    }

}
