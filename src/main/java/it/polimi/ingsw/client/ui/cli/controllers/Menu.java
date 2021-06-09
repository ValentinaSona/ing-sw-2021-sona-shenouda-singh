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
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;


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

    public void run(){
        while (true) {

            // Launches various menus
            switch (state) {
                case MAIN -> mainMenu();
                case SETUP -> setupMenu();
                case GAME -> gameMenu();
            }
            // If a menu arrives naturally to its end, wait for a state change to print the next one.
            waitStateChange();

            // Add here a catch for when a method is brutally interrupted.
        }
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
            case MAIN -> mainMenu();
            case SETUP -> setupMenu();
            case GAME -> gameMenu();
        }

    }

    private void gameMenu() {
        String[] options =
                {"See market", "See development card market", "See faith tracks", "See your board", "See other players boards", "Buy from market", "Buy Development Card", "Activate Productions", "Leader Action", "End Turn"};

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
            default -> gameMenu();
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
            case 1 -> {}
            case 2 -> {}
            case 3 -> mainMenu();
        }
    }


    public void setupMenu(){

        String[] options;

        if(((ServerSetupUserMessage) inMsg).getUser().getNickName().equals(MatchSettings.getInstance().getClientNickname())) {

            if (((ClientSetupActionMessage)outMsg)!= null && ((ClientSetupActionMessage)outMsg).getChosen()== null)
                cli.printMessage("[ ] You've received a selection of leader cards. Select \"Pick leader cards\" to continue.");

            if (((ServerSetupUserMessage) inMsg).getResources() != 0) {

                if (((ClientSetupActionMessage)outMsg)!= null && ((ClientSetupActionMessage)outMsg).getIdResourceMap()== null)
                    cli.printMessage("[ ] You've received " + ((ServerSetupUserMessage) inMsg).getResources() + " resources. Select \"Pick starting resources\" to continue.");
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
                    cli.printMessage(cli.getView().getFaithTrackView());
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
                    pickResources();

                    if (((ClientSetupActionMessage)outMsg).getChosen()!= null) {

                        var msg = ((ClientSetupActionMessage)outMsg);
                        UIController.getInstance().chosenStartingResources(msg.getIdResourceMap(), msg.getChosen());

                    } else setupMenu();

                    waitStateChange();

                }
            }

    }

    private void pickResources(){
        Map<Id, Resource> map = new HashMap<Id, Resource>();
        int max = ((ServerSetupUserMessage) inMsg).getResources();

        if (outMsg != null && ((ClientSetupActionMessage)outMsg).getIdResourceMap() != null) {
            cli.printMessage("[X] You have already picked your resources");
            return;
        }

        while (max > 0 ){
            Resource resource = cli.getResource(max);
            max -= resource.getQuantity();
            map.put(Id.DEPOT_2, resource);
        }

        var user = ((ServerSetupUserMessage)inMsg).getUser();

        if (outMsg!=null && ((ClientSetupActionMessage)outMsg).getChosen()!= null)
            // The user has already selected the leader cards.
            outMsg = new ClientSetupActionMessage(map, ((ClientSetupActionMessage)outMsg).getChosen(), user);
        else
            // The user still has to pick their leader cards.
            outMsg = new ClientSetupActionMessage(map, null, user);


        cli.printMessage("["+CHECK_MARK+"] Your resources have been placed in your depots. You'll be able to rearrange them freely once the game begins.");
    }

    private void pickLeaders() {
        var cards4 = ((ServerSetupUserMessage) inMsg).getLeaderCards();
        LeaderCard[] chosen = new LeaderCard[2];

        if (outMsg != null && ((ClientSetupActionMessage)outMsg).getChosen() != null) {
            cli.printMessage("[X] You have already picked your cards");
            return;
        }

        String[] cardOptions = new String[4];
        for (int i = 0; i < cards4.length; i++) {
            cardOptions[i]=cards4[i].toString();
        }
        cli.printMessage("[ ] Pick one from these leader cards: (1/2 cards to pick)");
        int choice = cli.getChoice(cardOptions);

        chosen[0] = cards4[choice-1];
        cards4[choice-1] = null;

        cardOptions = new String[4];
        for (int i = 0; i < cards4.length; i++) {
          if (cards4[i]!= null)  cardOptions[i]=cards4[i].toString();
          else cardOptions[i]= "You already picked this card!";
        }
        cli.printMessage("[ ] Pick one from these leader cards: (2/2 cards to pick)");

        int choice2;
        do {
            choice2 = cli.getChoice(cardOptions);
        } while (choice == choice2);

        chosen[1] = cards4[choice2-1];
        var user = ((ServerSetupUserMessage)inMsg).getUser();

        if (((ServerSetupUserMessage)inMsg).getResources() == 0)
            // The user doesn't have resources, so they don't need to pick them.
            outMsg = new ClientSetupActionMessage(new HashMap<>(), chosen, user);

        else if (outMsg!=null && ((ClientSetupActionMessage)outMsg).getIdResourceMap()!= null)
            // The user has resources and has already selected them.
            outMsg = new ClientSetupActionMessage(((ClientSetupActionMessage)outMsg).getIdResourceMap(), chosen, user);
        else
            // The user still has to pick their resources.
            outMsg = new ClientSetupActionMessage(null, chosen, user);

    }


}
