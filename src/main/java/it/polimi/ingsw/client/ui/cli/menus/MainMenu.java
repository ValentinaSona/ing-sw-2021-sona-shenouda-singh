package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;

import java.io.IOException;

public class MainMenu {


    private final CLI cli;

    public MainMenu(CLI cli) {
        this.cli = cli;
    }


    public void run(){
        mainMenu();
    }


    private void mainMenu(){
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
                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().sendNickname(nickname, "127.0.0.1", 10002);
                            MenuRunner.getInstance().waitResponse();
                        }
                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);
                    UIController.getInstance().joinLobby();
                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
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
            case 1 -> {
                String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                MenuRunner.getInstance().setSolo(true);
                UIController.getInstance().startLocalSinglePlayerGame(nickname);

            }
            case 2 -> {
                String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                try {
                    UIController.getInstance().sendNickname(nickname, "127.0.0.1", 10002);
                    MenuRunner.getInstance().setSolo(true);
                    UIController.getInstance().joinLobby();
                    UIController.getInstance().setCreation(1);
                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 3 -> mainMenu();
        }
    }

}
