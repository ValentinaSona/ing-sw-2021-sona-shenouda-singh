package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.ui.Ui;

import java.io.PrintStream;
import java.util.Scanner;

public class CLI implements Ui {
    private final PrintStream output;
    private final Scanner input;
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_RESET = "\033[0m";


    static String banner = "" +
            " __  __           _                         __   _   _            _____                  _                              \n" +
            "|  \\/  |         | |                       / _| | | | |          |  __ \\                (_)                             \n" +
            "| \\  / | __ _ ___| |_ ___ _ __ ___    ___ | |_  | |_| |__   ___  | |__) |___ _ __   __ _ _ ___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\|  _| | __| '_ \\ / _ \\ |  _  // _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) | |   | |_| | | |  __/ | | \\ \\  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|    \\__|_| |_|\\___| |_|  \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";

    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);
    }

    private void printMenu(String[] options){
        int optNum = 1;
        output.println("Choose an option:");
        for (String option : options){

            output.printf("\t%d)%s %s %s \n", optNum, ANSI_RED, option, ANSI_RESET);
            optNum++;
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
    }

    @Override
    public void start() {
        output.println(banner);
        String[] options = {"Play", "Resume","Quit"};
        printMenu(options);
    }
}
