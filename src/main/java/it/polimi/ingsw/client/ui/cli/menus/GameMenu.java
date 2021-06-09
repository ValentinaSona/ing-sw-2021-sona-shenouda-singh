package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

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
            default -> gameMenu();
        }
    }

}
