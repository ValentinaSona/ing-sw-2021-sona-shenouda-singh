package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.client.ui.cli.menus.MenuStates;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import javafx.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

/**
 * CLI user interface.
 * Holds all the methods to get and parse the user input.
 */
public class CLI implements Ui {
    /**
     * Connects to system out to print messages.
     */
    private final PrintStream output;
    /**
     * Connects to system in to get user input.
     */
    private final Scanner input;
    /**
     * Shortcut to the player's own player view.
     */
    private PlayerView view;

    /**
     * Whether another thread has asked to interrupt the getChoice input read.
     */
    private boolean interrupted;


    /**
     * Constructor that initializes the I/O .
     */
    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    }


    /**
     * Setter for the interrupted boolean. Used to interrupt getChoice.
     * @param interrupted interrupt value.
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Saves a reference to the player's view for ease of access.
     */
    public void setView(){
        String nick = MatchSettings.getInstance().getClientNickname();
        for (PlayerView playerView : GameView.getInstance().getPlayers()){
            if (playerView.getNickname().equals(nick)) this.view = playerView;
        }
    }

    /**
     * Getter for the player view
     * @return the player's view.
     */
    public PlayerView getView() {
        return view;
    }

    /**
     * Used throughout the code to print to stdout.
     * @param msg the message to be printed.
     */
    public void printMessage(Object msg){
        output.println(msg);
    }

    /**
     * Asks user for a resource (quantity + type) and its source/destination (depots and strongbox) and checks the input against an appropriate regex.
     * @param strongbox if the player is allowed to access the strongbox
     * @return the depot or strongbox id and the selected resource.
     */
    public Pair<Id, Resource> getIdResourcePair(boolean strongbox){
        boolean special = false;
        int special_num = 0;
        for (DepotView depot: view.getWarehouse()){
            if (depot.getId() == Id.S_DEPOT_1 || depot.getId() == Id.S_DEPOT_2 ) {
                special_num++;
                special = true;
            }
        }

        String regex = "";
        String desc;
        String[] choice;

        if (special) {
            if (strongbox){
                desc = "Format: <number> <resource type> @ <D or S><number> or <B>";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1]|[B])$";
                if (special_num == 2) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1-2]|[B])$";
            } else {
                desc = "Format: <number> <resource type> @ <D or S><number>";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1])$";
                if (special_num == 2) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1-2])$";
            }
        } else {
            if (strongbox){
                desc = "Format: <number> <resource type> @ <D><number> or <B>";
                regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[B])$";
            } else {
                desc = "Format: <number> <resource type> @ <D><number>";
                regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )[D][1-3]$";
            }
        }
        do {
            choice = getString(regex, desc).split(" ", 4);

        } while (Integer.parseInt(choice[0]) <= 0 );

        return new Pair<>(parseId(choice[3], parseResource(choice[0],choice[1])), parseResource(choice[0],choice[1]));

    }

    /**
     * Asks user input for a resource (quantity + type) and checks the input against a regex.
     * @param max the maximum valid quantity that the player can input.
     * @return the corresponding resource object.
     */
    public Resource getResource(int max){
        String[] choice;
        do {
           choice = getString("[0-9]+[ ](stone|coin|servant|shield)[s]?$", "Choose a valid resource (e.g. '2 servant|coin|shield|stones') up to a maximum of " + max).split(" ", 2);

        } while (Integer.parseInt(choice[0]) <= 0 || Integer.parseInt(choice[0]) > max);

        return parseResource(choice[0],choice[1]);
    }

    /**
     * Takes the two parts of the string representing a resource and returns the resource object.
     * Expects the strings to be correct.
     * @param number represents quantity.
     * @param type represents resource type
     * @return returns the parsed resource object.
     */
    private Resource parseResource(String number, String type){
        if (type.charAt(type.length() - 1) == 's') {
            type = type.substring(0, type.length() - 1);
        }

        return new Resource(Integer.parseInt(number) , ResourceType.parseInput(type));
    }

    /**
     * Parses a string corresponding to an ID.
     * @param id identifier string.
     * @param type type of the resource selected alongside the ID to identify the strongbox.
     * @return the depot or strongbox ID.
     */
    private Id parseId(String id, Resource type){
        if (id.charAt(0)=='D'){
            switch (id.charAt(1)){
                case '1' -> { return Id.DEPOT_1; }
                case '2' -> { return Id.DEPOT_2; }
                case '3' -> { return Id.DEPOT_3; }
            }
        } else if (id.charAt(0)=='S'){
            switch (id.charAt(1)) {
                case '1' -> { return Id.S_DEPOT_1; }
                case '2' -> { return Id.S_DEPOT_2; }
            }
        }else if (id.charAt(0)=='B'){
            switch (type.getResourceType()) {
                case COIN -> { return Id.STRONGBOX_COIN; }
                case SHIELD-> { return Id.STRONGBOX_SHIELD; }
                case STONE -> {return  Id.STRONGBOX_STONE;}
                case SERVANT -> {return Id.STRONGBOX_SERVANT;}
            }
        }
        return null;
    }

    /**
     * Asks the user the color and level of a development card.
     * @return an array of two int identifying row and column in the development card market matrix.
     */
    public int[] getDevelopmentRowCol(){
        String[] choice;
        do {
            choice = getString("[1-3] (green|blue|purple|yellow)$", "Choose a card by level and colour (e.g. '1 green|purple|blue|yellow')").split(" ", 2);

        } while (Integer.parseInt(choice[0]) > 3 || Integer.parseInt(choice[0]) < 1);
        int type = 0;
        switch (choice[1]){
            case "green" -> type = 0;
            case "blue" -> type = 1;
            case "yellow" -> type = 2;
            case "purple" -> type = 3;
        }
        return new int[]{Integer.parseInt(choice[0])-1,type};
    }

// TODO Test on the four player thingy cuz not working yet

    /**
     * Asks the player for a choice amongst the possible option presented to them.
     * The method, when called when the proper parameters, can be interrupted in order to refresh the options visible to the player (e.g. because it is now their turn).
     * To do so, it spawns an extra thread that uses the blocking hasNextInt() method in its stead.
     * It's usually called via its two overloaded methods.
     * @param options Text for each options. Its length is used to determine the possible inputs.
     * @param enableRefresh true the last time this method was called it was interrupted (and therefore the spawned thread is still waiting for input)
     * @param isMenu whether the method calling this one is a menu (and therefore knows how to handle a 0 return value).
     * @param interruptible whether or not this execution of the method can be interrupted.
     * @return either the chosen option number, 0 if the options need to be refreshed, or 1492 if we wish to end the game.
     */

    public int getChoice(String[] options, boolean enableRefresh, boolean isMenu, boolean interruptible){
        int optNum = 1;
        int choice;
        output.println("[ ] Choose an option:");
        // TODO: make this a single print so that messages cannot be printed inside the menu. Or synchronize it.
        for (String option : options){

            output.printf("\t%d) %s \n", optNum, option);
            optNum++;
        }
        boolean refreshed = false;
        while(true) {
            try {
                // The first two booleans avoid becoming blocked on System.in.available (happens if the thread's hasNextInt() is already using system.in) because java doesn't evaluate the second AND condition if the first one is false.
                // The interruptible boolean is used to avoid the process entirely if we wish for the choice not to be refreshable.
                if ((((!enableRefresh || refreshed ) && System.in.available()!=0)) || !interruptible) {

                    // This is the normal method execution. Enters here only if hasNextInt() won't be blocking.
                    refreshed = false;
                    enableRefresh = false;
                    if (input.hasNextInt()) {
                        choice = input.nextInt();

                    } else {
                        output.println("Input must be the number of a menu item");
                        input.next();
                        continue;
                    }
                    // If the selection is acceptable, exits the loop (must be included in the options OR be the in game menu secret code).
                    if ((choice > 0 && choice < optNum) || (choice == 1492 && isMenu && MenuRunner.getInstance().getState() == MenuStates.GAME)) break;
                    // otherwise print an error and loop.
                    else output.println("Input must be the number of a menu item");
                } else {
                    // Sets the interrupt flag to false. This is how we distinguish between normal and interrupting wake ups.
                    interrupted = false;

                    // Synchronize on a singleton so even the new thread can access it.
                    synchronized (CLIMessageHandler.getInstance()) {

                        // If enableRefresh is true, the method has been interrupted in its last usage and the thread is still running, so we need to avoid spawning another one.
                        if (!enableRefresh) {
                            // Marks the current position on system in.
                            //TODO: maybe place outside? woudl proably solve problems
                            System.in.mark(500);
                            // This thread will go in the blocking read instead of the main one.
                            Thread t = new Thread(() -> {
                                var input = new Scanner(System.in);
                                input.hasNextInt();
                                try {
                                    // Resets system in to the position marked before spawning the thread.
                                    System.in.reset();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // Notify the cli thread.
                                synchronized (CLIMessageHandler.getInstance()) {
                                    CLIMessageHandler.getInstance().notify();
                                }
                            });
                            t.start();
                        // Refreshed is set to true to indicate that the still working thread has been used, and to enter the normal program execution.
                        } else refreshed = true;

                        // Wait for an interrupt or for the system in thread to tell us there is an input available.
                        CLIMessageHandler.getInstance().wait();

                    }
                    // If we woke up because of an interruption and we are in a menu (meaning it will know to handle the 0 code)
                    if (interrupted && isMenu) return 0;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Special code handling
        if (choice == 1492) return choice;

        // Normal option handling.
        output.println("["+CHECK_MARK+"] Chosen: " + options[choice-1]);
        output.flush();
        input.nextLine();
        return choice;
    }

    /**
     * Calls the normal, non interruptible version of the method.
     * @param options Options amongst which to choose.
     * @return the option chosen (or the 1492 option).
     */
    public int getChoice(String[] options){
        return getChoice(options, false, false, false);
    }

    /**
     * Calls the interruptible version of the method.
     * @param options Options amongst which to choose.
     * @param enableRefresh whether or not it has been refreshed the last time it was called (usually set to true by the case 0 of the menu switch)
     * @param isMenu whether or not the caller handles the 0 option.
     * @return either the chosen option number, 0 if the options need to be refreshed, or 1492 if we wish to end the game.
     */
    public int getChoice(String[] options, boolean enableRefresh, boolean isMenu){
        return getChoice(options, enableRefresh, isMenu, true);
    }

    /**
     * General input request that matches the input against the supplied regex until a correct input has been supplied.
     * Used for the nickname and internally by the other input methods.
     * @param regex regex to check against.
     * @param desc description of the required format.
     * @return an input string that matches the regex.
     */
    public String getString(String regex, String desc){

        output.println("[ ] " + desc +":");
        String choice;
        while(true) {
            if (input.hasNextLine()) {
                choice = input.nextLine();
                if (choice.matches(regex)) break;
                else output.println("[X] "+ desc +":");
            }

        }


        output.println("["+CHECK_MARK+"] Chosen: " + choice);
        return choice;
    }

    /**
     * Asks the player for a yes or no answer.
     * @param desc yes or no question to be printed.
     * @return boolean corresponding to the chosen input.
     */
    public boolean getYesOrNo(String desc){
        String choice = getString("(yes|y|no|n)$", desc + "(yes/y/no/n)");
        return choice.charAt(0) == 'y';
    }

    /**
     * Asks the player for a number inside a range. Loops until the input is acceptable.
     * @param lower upper limit of the range (included)
     * @param upper lower limit of the range (included)
     * @return the selected valid int.
     */
    public int getInt(int lower, int upper){

        int choice = 0;
        while(true) {
            if(input.hasNextInt()) {
                choice = input.nextInt();
            } else {
                output.println("Input must range in between ["+ lower+  ","+upper+"]");
                input.next();
                continue;
            }
            if (choice >= lower && choice <= upper) break;
            else output.println("Input must range in between ["+ lower+  ","+upper+"]");
        }


        output.println("["+CHECK_MARK+"] Chosen: " + choice);
        output.flush();

        return choice;
    }


    /**
     * Prints the banner, initializes the message handler and launches the MenuRunner.
     */
    @Override
    public void start(){
        String banner = " __  __           _                         __   ____                  _                              \n" +
                "|  \\/  | __ _ ___| |_ ___ _ __ ___    ___  / _| |  _ \\ ___ _ __   __ _(_)___ ___  __ _ _ __   ___ ___ \n" +
                "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\| |_  | |_) / _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
                "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) |  _| |  _ <  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
                "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|   |_| \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";
        output.println(banner);

        CLIMessageHandler.getInstance(this);
        MenuRunner.getInstance(this).run();
    }

}
