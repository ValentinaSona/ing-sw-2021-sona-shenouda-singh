package it.polimi.ingsw.client.ui.cli;

import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.Ui;
import it.polimi.ingsw.client.ui.cli.menus.MenuRunner;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import javafx.util.Pair;

import java.io.*;
import java.util.Scanner;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;

public class CLI implements Ui {
    private final CLIMessageHandler msgHandler = CLIMessageHandler.getInstance(this);
    private final PrintStream output;
    private final Scanner input;

    private PlayerView view;
    private boolean interrupted;


    static String banner = " __  __           _                         __   ____                  _                              \n" +
            "|  \\/  | __ _ ___| |_ ___ _ __ ___    ___  / _| |  _ \\ ___ _ __   __ _(_)___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\| |_  | |_) / _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) |  _| |  _ <  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|   |_| \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n";

    public CLI()  {
        input = new Scanner(System.in);
        output = new PrintStream(System.out);


    }



    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
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

    public Pair<Id, Resource> getIdResourcePair(boolean strongbox, boolean special, int special_num){
        // TODO: add boolean if for devSlots
        String regex = "";
        String desc = "";
        String[] choice;

        if (special) {
            if (strongbox){
                desc = "Format: <number> <resource type> @ <D or S or B><number>";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1]|[B][1-4])$";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1-2]|[B])$";
            } else {
                desc = "Format: <number> <resource type> @ <D or S><number>";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1])$";
                if (special_num == 1) regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )([D][1-3]|[S][1-2])$";
            }
        } else {
            desc = "Format: <number> <resource type> @ <D><number>";
            regex = "[0-9]+[ ](stone|coin|servant|shield)[s]?( @ )[D][1-3]$";
        }
        do {
            choice = getString(regex, desc).split(" ", 4);

        } while (Integer.parseInt(choice[0]) <= 0 );

        return new Pair<>(parseId(choice[3], parseResource(choice[0],choice[1])), parseResource(choice[0],choice[1]));

    }

    public Resource getResource(int max){
        String[] choice;
        do {
           choice = getString("[0-9]+[ ](stone|coin|servant|shield)[s]?$", "Choose a valid resource (e.g. '2 servant|coin|shield|stones') up to a maximum of " + max).split(" ", 2);

        } while (Integer.parseInt(choice[0]) <= 0 || Integer.parseInt(choice[0]) > max);

        return parseResource(choice[0],choice[1]);
    }

    private Resource parseResource(String number, String type){
        if (type.charAt(type.length() - 1) == 's') {
            type = type.substring(0, type.length() - 1);
        }

        return new Resource(Integer.parseInt(number) , ResourceType.parseInput(type));
    }

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
        // Getting blocked because it tries system.inavailable while a thread is still running.
        while(true) {
            try {
                if ((((!enableRefresh || refreshed ) && System.in.available()!=0)) || !interruptible) {
                    // Normal program execution. Enters here only if hasNextInt() won't be blocking.
                    if (input.hasNextInt()) {
                        choice = input.nextInt();

                    } else {
                        output.println("Input must be the number of a menu item");
                        input.next();
                        continue;
                    }
                    if (choice > 0 && choice < optNum) break;
                    //if (enableHidden && choice == 0) break;
                    else output.println("Input must be the number of a menu item");
                } else {
                    // Sets the interrupt flag to false. This is how I distinguish between normal
                    // and interrupting wake ups.
                    interrupted = false;
                    // Synchronize on a singleton so even the new thread can access it.
                    synchronized (CLIMessageHandler.getInstance()) {

                        if (!enableRefresh) {
                            System.in.mark(500);
                            Thread t = new Thread(() -> {
                                var input = new Scanner(System.in);
                                input.hasNextInt();
                                try {
                                    System.in.reset();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                synchronized (CLIMessageHandler.getInstance()) {
                                    CLIMessageHandler.getInstance().notify();
                                }
                            });
                            t.start();
                        } else refreshed = true;
                        CLIMessageHandler.getInstance().wait();

                        //if (!interrupted) t.join();
                    }
                    if (interrupted && isMenu) return 0;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();

            }
        }


        output.println("["+CHECK_MARK+"] Chosen: " + options[choice-1]);
        output.flush();
        input.nextLine();
        return choice;
    }

    public int getChoice(String[] options){
        return getChoice(options, false, false, false);
    }

    public int getChoice(String[] options, boolean enableRefresh, boolean isMenu){
        return getChoice(options, enableRefresh, isMenu, true);
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

    public boolean getYesOrNo(String desc){
        String choice = getString("(yes|y|no|n)$", desc + "(yes/y/no/n)");
        return choice.charAt(0) == 'y';
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
