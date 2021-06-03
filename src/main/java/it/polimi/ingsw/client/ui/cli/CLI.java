package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.client.ui.cli.controllers.CLIMessageHandler;
import it.polimi.ingsw.client.ui.cli.controllers.Menu;
import it.polimi.ingsw.client.ui.controller.UIController;
import it.polimi.ingsw.server.controller.LeaderCardsController;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

import java.io.PrintStream;
import java.util.Scanner;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public class CLI implements Ui {
    private final CLIMessageHandler msgHandler = CLIMessageHandler.getInstance(this);
    private final PrintStream output;
    private final Scanner input;



    static String banner = " __  __           _                         __   ____                  _                              \n" +
            "|  \\/  | __ _ ___| |_ ___ _ __ ___    ___  / _| |  _ \\ ___ _ __   __ _(_)___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\| |_  | |_) / _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) |  _| |  _ <  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|   |_| \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";

    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);
    }

    public void printMessage(Object msg){
        output.println(msg);
    }


    public int getChoice(String[] options){
        int optNum = 1;
        int choice = 0;
        output.println("[ ] Choose an option:");
        for (String option : options){

            output.printf("\t%d) %s \n", optNum, option);
            optNum++;
        }

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
        output.flush();
        return choice;
    }

    public String getString(String regex, String desc){

        output.println(desc +":");
        String choice;
        input.nextLine();
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



    @Override
    public void start(){
        output.println(banner);


        Menu.getInstance(this).mainMenu();


    }

}
