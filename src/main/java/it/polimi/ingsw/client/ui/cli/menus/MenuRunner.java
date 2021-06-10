package it.polimi.ingsw.client.ui.cli.menus;


import it.polimi.ingsw.client.modelview.DepotView;
import it.polimi.ingsw.client.modelview.GameView;
import it.polimi.ingsw.client.modelview.MatchSettings;
import it.polimi.ingsw.client.modelview.PlayerView;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.server.model.Id;
import it.polimi.ingsw.server.model.LeaderCard;
import it.polimi.ingsw.server.model.Resource;
import it.polimi.ingsw.server.model.ResourceType;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerMessage;

import static it.polimi.ingsw.client.ui.cli.CLIHelper.*;
import static it.polimi.ingsw.client.ui.cli.menus.MenuStates.MAIN;


public class MenuRunner {

    private static MenuRunner singleton;
    private final CLI cli;

    private MenuStates state = MAIN;

    private GameActions lastAction;
    private GameActions presentAction;

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
    public GameActions getLastAction() {
        return lastAction;
    }

    public void setLastAction(GameActions lastAction) {
        this.lastAction = lastAction;
    }

    public GameActions getPresentAction() { return presentAction; }

    public void setPresentAction(GameActions presentAction) { this.presentAction = presentAction; }

    public ServerMessage getInMsg() {
        return inMsg;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
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
            waitStateChange();
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
                if (lastAction == presentAction) wait = false;
            } catch (InterruptedException e) {

            }
        }
    }


    public void printFaithTracks(){
        for (PlayerView player : GameView.getInstance().getPlayers()){
            if(player.getNickname().equals(MatchSettings.getInstance().getClientNickname()))
                cli.printMessage("\t Your Faith Track:");
            else
                cli.printMessage("\t" + player.getNickname() +"'s Faith Track:");
            cli.printMessage(player.getFaithTrackView());
        }
    }

    public void printHand(){
        var cards = cli.getView().getLeaderCards();
        int i = 0;

        for (LeaderCard card : cards){
            if (!card.isActive()) {
                cli.printMessage(card);
                i++;
            }
        }

        if (i == 0) cli.printMessage("[X} There are no cards in your hand");
    }

    public void printPlayedLeaders(){
        var cards = cli.getView().getLeaderCards();
        int i = 0;
        for (LeaderCard card : cards){
            if (card.isActive()) {
                cli.printMessage(card);
                i++;
            }
        }

        if (i == 0) cli.printMessage("[X] You have played no leader cards");
    }

    public void printDepots(){
        var warehouse = cli.getView().getWarehouse();
        String output = "";
        String content;
        String padding =  new Resource(1, ResourceType.SERVANT).toString();
        String filler = " ";

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
                output += "\t\t      "+ SQUARE+ " " + content+filler + " " + SQUARE + "\t Depot 1, capacity: 1\n";
            }

            if(depot.getId()== Id.DEPOT_2){
                output += "\t\t    "+ SQUARE+ " " + SQUARE+ " " +content+filler +" " + SQUARE + " " + SQUARE + "\t Depot 2, capacity: 2\n";
            }

            if(depot.getId()== Id.DEPOT_3){

                output += "\t\t  "+ SQUARE+ " " + SQUARE+ " " + SQUARE+ " "+ content+filler + " " +SQUARE+ " " + SQUARE+ " " + SQUARE+ " \t Depot 3, capacity: 3\n";
            }

            if(depot.getId()== Id.S_DEPOT_1){
                output += "\t\t    "+ SQUARE+ " " + SQUARE+ " " +content+filler +" " + SQUARE + " " + SQUARE + "\t Special depot 1, capacity: 2, type: " + depot.getResource().getResourceType() +"\n";
            }

            if(depot.getId()== Id.S_DEPOT_2){
                output += "\t\t    "+ SQUARE+ " " + SQUARE+ " " +content+filler +" " + SQUARE + " " + SQUARE + "\t Special depot 2, capacity: 2, type: " + depot.getResource().getResourceType() +"\n";
            }

        }
        cli.printMessage(output);

    }

}
