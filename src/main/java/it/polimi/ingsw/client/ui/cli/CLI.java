package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;

import java.io.PrintStream;
import java.util.Scanner;

public class CLI implements Ui {
    private final PrintStream output;
    private final Scanner input;
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_GREEN = "\033[32m";
    public static final String ANSI_YELLOW = "\033[33m";
    public static final String ANSI_BLUE = "\033[34m";
    public static final String ANSI_PURPLE = "\033[35m";
    public static final String ANSI_CYAN = "\033[36m";
    public static final String ANSI_WHITE = "\033[37m";
    public static final String ANSI_RESET = "\033[0m";
    public static final String CHECK_MARK = "\u2713";



    static String banner = " __  __           _                         __   ____                  _                              \n" +
            "|  \\/  | __ _ ___| |_ ___ _ __ ___    ___  / _| |  _ \\ ___ _ __   __ _(_)___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\| |_  | |_) / _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) |  _| |  _ <  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|   |_| \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";

    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);
    }

    private int getChoice(String[] options){
        int optNum = 1;
        int choice = 0;
        output.println("[ ] Choose an option:");
        for (String option : options){

            output.printf("\t%d) %s \n", optNum, option);
            optNum++;
        }
        output.println();

        while(true) {
            if(input.hasNextInt()) {
                choice = input.nextInt();
            } else {
                output.println("Input must be the number of a menu item");
                input.next();
                continue;
            }
            if (choice > 0 && choice < optNum) break;
            else output.println("Input must be the number of a menu item");
        }


        output.println("["+CHECK_MARK+"] Chosen: " + options[choice-1]);
        return choice;
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }

    @Override
    public void start() {
        output.println(banner);
        String[] options = {"Singleplayer", "Multiplayer","Credits", "Quit"};
        getChoice(options);

    }
}
