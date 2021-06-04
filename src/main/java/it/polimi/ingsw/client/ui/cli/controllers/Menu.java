package it.polimi.ingsw.client.ui.cli.controllers;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.game.ClientSetupActionMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerSetupUserMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.MARBLE_LEGEND;


public class Menu {

    private static Menu singleton;
    private final CLI cli;
    private MenuStates state = MenuStates.MAIN;
    private ServerMessage inMsg;
    private ClientMessage outMsg;
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
        this.inMsg = msg;
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

        if(((ServerSetupUserMessage) inMsg).getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {

            if (((ServerSetupUserMessage) inMsg).getResources() != 0) {
                options = new String[]{"See market", "See development card market", "See faith track", "Pick leader cards", "Pick starting resources"};
            } else
                options = new String[]{"See market", "See development card market", "See faith track", "Pick leader cards"};
        } else {
            options = new String[]{"See market", "See development card market", "See faith track"};
        }

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
                case 3 -> {
                    setupMenu();
                }
                case 4 -> {
                    pickLeaders();
                    if (((ClientSetupActionMessage)outMsg).getIdResourceMap()!= null) {
                        var msg = ((ClientSetupActionMessage)outMsg);
                        UIController.getInstance().chosenStartingResources(msg.getIdResourceMap(), msg.getChosen());
                    } else setupMenu();

                    waitStateChange();

                }
                case 5 -> {
                    setupMenu();
                }
            }

    }

    private void pickLeaders() {
        var cards4 = ((ServerSetupUserMessage) inMsg).getLeaderCards();
        LeaderCard[] chosen = new LeaderCard[2];

        if (cards4 == null) {
            cli.printMessage("[X] You have already picked your cards");
        }

        String[] cardOptions = new String[4];
        for (int i = 0; i < cards4.length; i++) {
            cardOptions[i]=cards4[i].toString();
        }
        cli.printMessage("[ ] Pick one from these leader cards: (1/2 cards to pick)");
        int choice = cli.getChoice(cardOptions);

        chosen[0] = cards4[choice-1];
        cards4[choice-1] =null;

        cardOptions = new String[4];
        for (int i = 0; i < cards4.length; i++) {
          if (cards4[i]!= null)  cardOptions[i]=cards4[i].toString();
          else cardOptions[i]= "You already picked this card!";
        }
        cli.printMessage("[ ] Pick one from these leader cards: (2/2 cards to pick)");

        int choice2 = 0;
        do {
            choice2 = cli.getChoice(cardOptions);
        } while (choice == choice2);

        chosen[1] = cards4[choice2-1];
        var user = ((ServerSetupUserMessage)inMsg).getUser();

        if (((ServerSetupUserMessage)inMsg).getResources() == 0)
            // The user doesn't have resources, so they don't need to pick them.
            outMsg = (ClientMessage) new ClientSetupActionMessage(new HashMap<Id, Resource>(), chosen, user);

        else if (outMsg!=null && ((ClientSetupActionMessage)outMsg).getIdResourceMap()!= null)
            // The user has resources and has already selected them.
            outMsg = (ClientMessage) new ClientSetupActionMessage(((ClientSetupActionMessage)outMsg).getIdResourceMap(), chosen, user);
        else
            // The user still has to pick their resources.
            outMsg = (ClientMessage) new ClientSetupActionMessage(null, chosen, user);
    }


}
