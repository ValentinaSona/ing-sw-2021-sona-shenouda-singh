package it.polimi.ingsw.client.ui.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class CLI implements Runnable {
    private final PrintStream output;
    private final Scanner input;
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_RESET = "\033[0m";
    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);
    }

    private void printMenu(String[] options){
        int optNum = 1;
        for (String option : options){
            output.printf("%d)%s %s %s \n", optNum, ANSI_RED, option, ANSI_RESET);
            optNum++;
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.run();
    }

    @Override
    public void run() {
        String[] options = {"Play", "Resume","Quit"};
        printMenu(options);
    }
}
