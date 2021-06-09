package it.polimi.ingsw.client.ui.cli.menus;


import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;
import static it.polimi.ingsw.client.ui.cli.menus.MenuStates.MAIN;
import static it.polimi.ingsw.client.ui.cli.menus.MenuStates.SETUP;


public class MenuRunner {

    private static MenuRunner singleton;
    private final CLI cli;
    private MenuStates state = MAIN;

    private final GameMenu gameMenu;
    private final MainMenu mainMenu;
    private final SetupMenu setupMenu;

    private ServerMessage inMsg;
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

    public ServerMessage getInMsg() {
        return inMsg;
    }

    public MenuRunner(CLI cli) {
        this.cli = cli;

        this.gameMenu = new GameMenu(cli);
        this.mainMenu = new MainMenu(cli);
        this.setupMenu = new SetupMenu(cli);
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

    public void run(){
        while (true) {

            // Launches various menus
            switch (state) {
                case MAIN -> mainMenu.run();
                case SETUP -> setupMenu.run();
                case GAME -> gameMenu.run();
            }
            // If a menu arrives naturally to its end, wait for a state change to print the next one.
            if (state == MAIN ) waitStateChange();
            if (state == SETUP ) waitStateChange();

        }
    }


    private void waitStateChange(){
        wait = true;
        synchronized (MenuRunner.getInstance()) {
            while (wait) {
                try {
                    MenuRunner.getInstance().wait();
                } catch (InterruptedException e) {

    } } } }

    public void printFaithTracks(){
        for (PlayerView player : GameView.getInstance().getPlayers()){
            if(player.getNickname().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("\t Your Faith Track:");
            else
                cli.printMessage("\t" + player.getNickname() +"'s Faith Track:");
            cli.printMessage(player.getFaithTrackView());
        }
    }

}
