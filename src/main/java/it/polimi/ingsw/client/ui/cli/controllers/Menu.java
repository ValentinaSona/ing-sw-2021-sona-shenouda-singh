package it.polimi.ingsw.client.ui.cli.controllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Market;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

import java.io.IOException;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;


public class Menu {

    private static Menu singleton;
    private final CLI cli;
    private MenuStates state = MenuStates.MAIN;
    private ServerMessage msg;
    boolean wait = false;


    public MenuStates getState() {
        return state;
    }

    public void setState(MenuStates state) {
        this.state = state;
        this.wait = false;
    }

    public void setState(MenuStates state, ServerMessage msg) {
        this.state = state;
        this.wait = false;
        this.msg = msg;
    }


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


    public void mainMenu(){
        String[] options = {"Singleplayer", "Multiplayer","Credits", "Quit"};

        switch (cli.getChoice(options)){
            case 1 -> singleMenu();
            case 2 -> multiMenu();
            case 3 -> credits();
            case 4 -> System.exit(0);
        }

        waitStateChange();

    }

    private void waitStateChange(){
        wait = true;
        synchronized (Menu.getInstance()) {
            while (wait) {
                try {
                    Menu.getInstance().wait();
                } catch (InterruptedException e) {

                }
            }
        }


        switch (state){
            case SETUP -> setupMenu();
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
                    UIController.getInstance().sendNickname(nickname, "127.0.0.1", 10001);
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
        if (((ServerSetupUserMessage)msg).getResources() != 0) {
            options =new String[]{"See market", "See development card market","See faith track", "Pick leader cards", "Pick starting resources"};
        } else options =new String[]{"See market", "See development card market","See faith track", "Pick leader cards"};

            switch (cli.getChoice(options)) {
                case 1 -> {

                    cli.printMessage(MARBLE_LEGEND);
                    cli.printMessage(GameView.getInstance().getMarketInstance());
                    setupMenu();
                }
                case 2 -> {
                    cli.printMessage(GameView.getInstance().getDevelopmentCardsMarket());
                    setupMenu();
                }
                case 3, 5, 4 -> {
                    setupMenu();
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
