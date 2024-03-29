package it.polimi.ingsw.client.ui.cli.menus;

import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.utils.Constant;
import it.polimi.ingsw.utils.GameActions;

import java.io.IOException;

/**
 * Implements the client starting menu, letting the player choose which mode they wish to play in.
 * Handles the first server connection until when the lobby becomes full, the setup menu takes over.
 */
public class MainMenu {


    private final CLI cli;
    private boolean returned;

    public MainMenu(CLI cli) {
        this.cli = cli;
        returned = false;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void run(){
        mainMenu();
    }

    private void mainMenu(){
        String[] options = {"Singleplayer", "Multiplayer","Credits", "Quit"};

        switch (cli.getChoice(options, returned, true)){
            case 1 -> singleMenu();
            case 2 -> multiMenu();
            case 3 -> credits();
            case 4 -> System.exit(0);
        }
        returned = false;
    }

    private void credits() {
        cli.printMessage("Implementation of the game \"Maestri del Rinascimento\" ");
        cli.printMessage("By Valentina Sona, Antony Shenouda and Raul Singh");
        mainMenu();
    }

    private void multiMenu() {
        String[] options = {"Play", "Reconnect", "Resume","Back to main menu"};

        switch (cli.getChoice(options)){
            case 1 -> {
                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().sendNickname(nickname, Constant.hostIp(), Constant.port());
                            MenuRunner.getInstance().waitResponse();
                        }
                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);

                    UIController.getInstance().joinLobby();

                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 2 -> {

                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "What was your nickname? (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().reconnectToServer(nickname, Constant.hostIp(), Constant.port());
                            MenuRunner.getInstance().waitResponse();
                        }


                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);
                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 3 -> {
                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().sendNickname(nickname, Constant.hostIp(), Constant.port());
                            MenuRunner.getInstance().waitResponse();
                        }
                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);

                    MenuRunner.getInstance().setState(MenuStates.RESUME);
                    UIController.getInstance().resumeGameFromFile();

                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 4 -> mainMenu();
        }
    }

    private void singleMenu() {
        String[] options = {"Local game", "Remote game", "Reconnect", "Resume", "Back to main menu"};

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
                    UIController.getInstance().sendNickname(nickname, Constant.hostIp(), Constant.port());
                    MenuRunner.getInstance().setSolo(true);
                    UIController.getInstance().joinLobby();
                    UIController.getInstance().setCreation(1);
                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 3 -> {

                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "What was your nickname? (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().reconnectToServer(nickname, Constant.hostIp(), Constant.port());
                            MenuRunner.getInstance().waitResponse();
                        }


                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);
                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 4 -> {
                try {
                    do {
                        String nickname = cli.getString("^[a-zA-Z0-9 _.-]{1,20}$", "Choose a nickname (Max 20 characters)");
                        if (nickname.equals("a nickname")) System.out.println("You're a funny one, aren't you?");
                        MenuRunner.getInstance().setContextAction(GameActions.MENU);
                        MenuRunner.getInstance().setCurrentAction(GameActions.WAITING);
                        synchronized (MenuRunner.getInstance()) {
                            UIController.getInstance().sendNickname(nickname, Constant.hostIp(), Constant.port());
                            MenuRunner.getInstance().waitResponse();
                        }
                    } while (MenuRunner.getInstance().getCurrentAction() == GameActions.END_TURN);

                    MenuRunner.getInstance().setState(MenuStates.RESUME);
                    UIController.getInstance().resumeGameFromFile();

                } catch (IOException e) {
                    cli.printMessage("[X] Unable to connect to server. Returning to main menu.");
                    mainMenu();
                }
            }
            case 5 -> mainMenu();
        }
    }

}
