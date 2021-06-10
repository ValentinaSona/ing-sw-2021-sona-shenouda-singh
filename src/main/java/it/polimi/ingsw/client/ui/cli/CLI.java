package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerWarehouseMessage;

import java.io.*;
import java.util.Scanner;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public class CLI implements Ui {
    private final CLIMessageHandler msgHandler = CLIMessageHandler.getInstance(this);
    private final PrintStream output;
    private final Scanner input;

    private PlayerView view;



    static String banner = " __  __           _                         __   ____                  _                              \n" +
            "|  \\/  | __ _ ___| |_ ___ _ __ ___    ___  / _| |  _ \\ ___ _ __   __ _(_)___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\| |_  | |_) / _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) |  _| |  _ <  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|   |_| \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";

    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);


    }


    public void setView(){
        String nick = MatchSettings.getInstance().getClientNickname();
        for (PlayerView playerView : GameView.getInstance().getPlayers()){
            if (playerView.getNickname().equals(nick)) this.view = playerView;
        }
    }

    public PlayerView getView() {
        return view;
    }

    public void printMessage(Object msg){
        output.println(msg);
    }

    public Resource getResource(int max){
        String[] choice;
        do {
           choice = getString("[0-9]+[ ](stone|coin|servant|shield)[s]?$", "Choose a valid resource (e.g. '2 servant|coin|shield|stones') up to a maximum of " + max).split(" ", 2);

        } while (Integer.parseInt(choice[0]) <= 0 || Integer.parseInt(choice[0]) > max);

        if (choice[1] != null && choice[1].length() > 0 && choice[1].charAt(choice[1].length() - 1) == 's') {
            choice[1] = choice[1].substring(0, choice[1].length() - 1);
        }

        return new Resource(Integer.parseInt(choice[0]) , ResourceType.parseInput(choice[1]));
    }

    public int getChoice(String[] options, boolean enableHidden){
        int optNum = 1;
        int choice;
        output.println("[ ] Choose an option:");
        // TODO: make this a single print so that messages cannot be printed inside the menu. Or synchronize it.
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
            if (enableHidden && choice == 0) break;
            else output.println("Input must be the number of a menu item");
        }


        if (choice !=0) output.println("["+CHECK_MARK+"] Chosen: " + options[choice-1]);
        output.flush();
        input.nextLine();
        return choice;
    }
    public int getChoice(String[] options){
        return getChoice(options, false);
    }

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
        MenuRunner.getInstance(this).run();
    }

}
