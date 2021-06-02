package it.polimi.ingsw.client.ui.cli.controllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;

import java.io.IOException;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;

public class Menu {

    private static Menu singleton;
    private final CLI cli;



    private boolean setupTurn = false;

    public Menu(CLI cli) {
        this.cli = cli;
    }


    public static Menu getInstance(CLI cli){
        if(singleton == null){
            singleton = new Menu(cli);
        }
        return singleton;
    }
    public static Menu getInstance(){
        return singleton;
    }

    public void setSetupTurn(boolean setupTurn) {
        this.setupTurn = setupTurn;
    }


    public void mainMenu(){
        String[] options = {"Singleplayer", "Multiplayer","Credits", "Quit"};

        switch (cli.getChoice(options)){
            case 1 -> singleMenu();
            case 2 -> multiMenu();
            case 3 -> credits();
            case 4 -> System.exit(0);
        }
    }

    private void credits() {
    }

    private void multiMenu() {
        String[] options = {"Play", "Resume","Back to main menu"};

        switch (cli.getChoice(options)){
            case 1 -> {
                String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                try {
                    UIController.getInstance().sendNickname(nickname, "127.0.0.1", 10000);
                    UIController.getInstance().joinLobby();
                } catch (IOException e) {
                    cli.printMessage("Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 2 -> {}
            case 3 -> mainMenu();
        }
    }

    private void singleMenu() {
        String[] options = {"Local game", "Remote game","Back to main menu"};

        switch (cli.getChoice(options)){
            case 1 -> {}
            case 2 -> {}
            case 3 -> mainMenu();
        }
    }

    public void setupMenu(){
        String[] options;

        while (true) {

            if (setupTurn) options = new String[]{"See market", "See development card market","See faith track", "Pick starting resources"};
            else options = new String[]{"See market", "See development card market","See faith track"};

            switch (cli.getChoice(options)) {
                case 1 -> {

                    cli.printMessage(MARBLE_LEGEND);
                    cli.printMessage(GameView.getInstance().getMarketInstance());
                }
                case 2 -> {
                }
                case 3 -> mainMenu();
                case 4 -> {
                    setupMenuPick();
                    break;
                }
            }
        }
    }

    private void setupMenuPick() {
        String[] options = {"Pick resources", "Pick leader cards","Back to previous menu"};

        switch (cli.getChoice(options)){
            case 1 -> {}
            case 2 -> {}
            case 3 -> {}
        }

    }


}
