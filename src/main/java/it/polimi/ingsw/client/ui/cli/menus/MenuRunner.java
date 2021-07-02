package it.polimi.ingsw.client.ui.cli.menus;


import it.polimi.ingsw.client.modelview.*;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.utils.GameActions;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;
import static it.polimi.ingsw.client.ui.cli.menus.MenuStates.*;

/**
 * This class handles moving between different menus, and therefore states.
 * Also contains various non static printing utilities.
 */
public class MenuRunner {

    private static MenuRunner singleton;
    /**
     * The CLI class for the print utilities.
     */
    private final CLI cli;

    /**
     * Enumeration used to know which menu must be run/
     */
    private MenuStates state = MAIN;
    /**
     * The action that the player is trying to attempt
     */
    private GameActions contextAction;
    /**
     * The actual state of the player's action.
     */
    private GameActions currentAction;


    private final GameMenu gameMenu;
    private final MainMenu mainMenu;
    private final SetupMenu setupMenu;

    private ServerMessage inMsg;

    /**
     * If the game is solo
     */
    private boolean solo;
    /**
     * Used to synchronize menus and wait for response messages from the server.
     */
    private boolean wait = false;


    public MenuStates getState() {
        return state;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
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

    public void setInMsg(ServerMessage inMsg) {
        this.inMsg = inMsg;
    }

    public boolean isSolo() {
        return solo;
    }

    public void setSolo(boolean solo) {
        this.solo = solo;
    }

    public GameActions getContextAction() {
        return contextAction;
    }

    public void setContextAction(GameActions contextAction) {
        this.contextAction = contextAction;
    }

    public GameActions getCurrentAction() { return currentAction; }

    public void setCurrentAction(GameActions currentAction) { this.currentAction = currentAction; }

    public ServerMessage getInMsg() {
        return inMsg;
    }

    /**
     * Initializes all the menus
     * @param cli reference to the cli in order to use the I/O functions
     */
    public MenuRunner(CLI cli) {
        this.cli = cli;

        this.gameMenu = new GameMenu(cli);
        this.mainMenu = new MainMenu(cli);
        this.setupMenu = new SetupMenu(cli);
        this.solo = false;
    }


    public static MenuRunner getInstance(CLI cli){
        if(singleton == null){
            singleton = new MenuRunner(cli);
        }
        return singleton;
    }
    public static MenuRunner getInstance(){
        return singleton;
    }

    /**
     * Handles switching between menus.
     * Usually goes into a wait when a menu exits, expecting the state to change before resuming.
     */
    public void run(){


        while (true) {

            // Launches various menus
            switch (state) {
                case MAIN -> mainMenu.run();
                case SETUP -> setupMenu.run();
                case GAME -> gameMenu.run();
                case REJOIN -> gameMenu.depositResources();
            }

            // If a menu arrives naturally to its end, wait for a state change to print the next one.
            if (state != END && state != REJOIN) waitStateChange();
            // If i am rejoining I already know the next menu to run is the game one.
            else if (state == REJOIN) {
                state = GAME;
                gameMenu.run();
            }
            // The end state resets the menuRunner back to its original state, printing the main menu.
            else {
                state = MAIN;
                setupMenu.setDone(false);
                mainMenu.setReturned(true);
            }
        }
    }


    /**
     * Wait before going to the next menu.
     * Is woken by the setState()
     */
    private void waitStateChange(){
        wait = true;
        synchronized (MenuRunner.getInstance()) {
            while (wait) {
                try {
                    MenuRunner.getInstance().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
    } } } }

    /**
     *  Called when a method requires to receive the response before proceeding.
     *  ALWAYS to be called inside a synchronized, since it doesn't synchronize itself.
     *  This is so that the synchronized block can easily include the various methods that send the message to the server for maximum thread safety.
     */
    public void waitResponse(){
        wait = true;
        while (wait) {
            try {
                MenuRunner.getInstance().wait();
                if (contextAction == currentAction || MenuRunner.getInstance().getState()==END) wait = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to unlock from waitResponse().
     * @param action action being unlocked.
     * @param msg informational message that will be printed.
     */
    public void sendResponse(GameActions action, String msg){
        synchronized (MenuRunner.getInstance()) {
            setCurrentAction(action);
            MenuRunner.getInstance().notifyAll();
            cli.printMessage(msg);
        }
    }

    /**
     * Used to unlock from waitResponse() when there's multiple possible outcomes for an action.
     * @param action action being unlocked.
     * @param newAction new state that will be set to show what kind of response was given.
     * @param msg informational message that will be printed.
     */
    public void sendResponse(GameActions action, GameActions newAction, String msg){
        if (contextAction == action) wait = false;
        sendResponse(newAction, msg);
    }



    public void printBoard(PlayerView view){
        cli.printMessage("Development card slots:");
        printProductions(view);
        cli.printMessage("\nPlayed leader cards:");
        printPlayedLeaders(view);
        cli.printMessage("Warehouse and strongbox:");
        printDepots(view);
        printStrongbox(view);
    }

    public void printBoard(){
        printBoard(cli.getView());
    }
    public void printBoards(){
        for (PlayerView player : GameView.getInstance().getPlayers()){
            if(!player.getNickname().equals(MatchSettings.getInstance().getClientNickname())) {
                cli.printMessage("\t" + player.getNickname() + "'s Board:");
                printBoard(player);
            }
        }
    }

    public void printFaithTracks(){

        String Lorenzo = " ".repeat(2*GameView.getInstance().getBlackCross()+1);
        Lorenzo += ANSI_BLACK + CROSS + ANSI_RESET + " " + GameView.getInstance().getBlackCross();
        for (PlayerView player : GameView.getInstance().getPlayers()){
            if(player.getNickname().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("\t Your Faith Track:");
            else
                cli.printMessage("\t" + player.getNickname() +"'s Faith Track:");
            if (MatchSettings.getInstance().isSolo())
                cli.printMessage(Lorenzo);
            cli.printMessage(player.getFaithTrackView());
        }
    }

    public void printHand(PlayerView view){
        var cards = view.getLeaderCards();
        int i = 0;

        for (LeaderCard card : cards){
            if (card!=null && !card.isActive()) {
                cli.printMessage(card);
                i++;
            }
        }

        if (i == 0) cli.printMessage("[X} There are no cards in your hand");
    }
    public void printHand(){printHand(cli.getView());}

    public void printPlayedLeaders(PlayerView view){
        var cards = view.getLeaderCards();
        int i = 0;
        if (cards == null) { cli.printMessage("[X] You have played no leader cards"); return;}
        for (LeaderCard card : cards){
            if (card != null && card.isActive()) {
                cli.printMessage(card);
                i++;
            }
        }

        if (i == 0) cli.printMessage("[X] You have played no leader cards");
    }

    public void printDepots(PlayerView view){
        var warehouse = view.getWarehouse();
        StringBuilder output = new StringBuilder();
        String content;
        String padding =  new Resource(1, ResourceType.SERVANT).toString();
        String filler;

        for (DepotView depot : warehouse){

            if (depot.getResource()!=null) {
                content = depot.getResource().toString();
                filler = " ".repeat(padding.length() - content.length());
            }
            else {
                content = " ".repeat(9);
                filler = "";
            }


            if(depot.getId()== Id.DEPOT_1){
                output.append("\t\t" + SQUARE + " ").append(content).append(filler).append(" ").append(SQUARE).append("         Depot 1, capacity: 1\n");
            }

            if(depot.getId()== Id.DEPOT_2){
                output.append("\t\t" + SQUARE + " ").append(content).append(filler).append(" ").append(SQUARE).append(" ").append(SQUARE).append("       Depot 2, capacity: 2\n");
            }

            if(depot.getId()== Id.DEPOT_3){

                output.append("\t\t" + SQUARE + " ").append(content).append(filler).append(" ").append(SQUARE).append(" ").append(SQUARE).append(" ").append(SQUARE).append("     Depot 3, capacity: 3\n");
            }

            if(depot.getId()== Id.S_DEPOT_1){
                output.append("\n\t\t" + SQUARE + " ").append(content).append(filler).append(" ").append(SQUARE).append(" ").append(SQUARE).append("       Special depot 1, capacity: 2, type: ").append(depot.getResource().getResourceType()).append("\n");
            }

            if(depot.getId()== Id.S_DEPOT_2){
                output.append("\t\t" + SQUARE + " ").append(content).append(filler).append(" ").append(SQUARE).append(" ").append(SQUARE).append("        Special depot 2, capacity: 2, type: ").append(depot.getResource().getResourceType()).append("\n");
            }

        }
        cli.printMessage(output.toString());

    }
    public void printDepots(){printDepots(cli.getView());}

    public void printStrongbox(PlayerView view){
        var box = view.getStrongboxView();
        String output = "";

        output += "\t\t"+ SQUARE+ " " + box.getCoin() + "    "+ SQUARE +" ";
        output += "\t"+ SQUARE+ " " + box.getStone() + "   "+ SQUARE +" ";
        output += "\t"+ SQUARE+ " " + box.getServant() + " "+ SQUARE +" ";
        output += "\t"+ SQUARE+ " " + box.getShield() + "  "+ SQUARE +"\n";

        cli.printMessage(output);
    }
    public void printStrongbox(){printStrongbox(cli.getView());}

    public void printProductions(PlayerView view){

        var slots = view.getSlots();

        cli.printMessage("\nBoard Production:\n 1 JOLLY + 1 JOLLY -> 1 JOLLY");

        DevelopmentCardSlotView slot = (DevelopmentCardSlotView) slots.get(1);
        cli.printMessage("\nSlot 1: ");
        if (slot.peek() != null)
            cli.printMessage("\n" + slot.peek().toString() + "\tHidden cards value: " + slot.hiddenVP()+" VP " + slot.hiddenColors() );
        else
            cli.printMessage("empty \n");

        slot = (DevelopmentCardSlotView) slots.get(2);
        cli.printMessage("\nSlot 2: ");
        if (slot.peek() != null)
            cli.printMessage("\n" + slot.peek().toString()+ "\tHidden cards value: " + slot.hiddenVP()+" VP " + slot.hiddenColors() );
        else
            cli.printMessage("empty \n");


        slot = (DevelopmentCardSlotView) slots.get(3);
        cli.printMessage("\nSlot 3: ");
        if (slot.peek() != null)

            cli.printMessage("\n" + slot.peek().toString()+ "\tHidden cards value: " + slot.hiddenVP()+" VP " + slot.hiddenColors() );

        else
            cli.printMessage("empty \n");

        SpecialProductionView special;
        if (slots.size() >= 5) {
            special = (SpecialProductionView) slots.get(4);
            cli.printMessage("\nSpecial slot 1:\n \t" + special.getSpecialProduction());
        }
        if (slots.size() >= 6) {
            special = (SpecialProductionView) slots.get(5);
            cli.printMessage("\nSpecial slot 2:\n \t" + special.getSpecialProduction());
        }

    }

}
