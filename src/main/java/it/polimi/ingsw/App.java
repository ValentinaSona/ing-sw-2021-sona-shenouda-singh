package it.polimi.ingsw;


import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.gui.GUI;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.Constant;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Program's main class that handles the command line arguments.
 * Launches the two types of clients and the server and initializes the logger.
 * Optionally changes the default port and hostname of the server.
 */
public class App 
{
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static void main( String[] args ) throws IOException {
        // Called to disable all logging messages
        LogManager.getLogManager().reset();
        LOGGER.log(Level.INFO, "Logger initialized");

        if (args.length == 3){
            Constant.setHostIp(args[1]);
            Constant.setPort(Integer.parseInt(args[2]));
        }

        if (args.length == 0){
            startGUI();
        }

        switch (args[0]) {
            case "gui" -> startGUI();
            case "server" -> new Server(Constant.port()).start();
            case "cli" -> {
                Client client = new Client(new CLI());
                DispatcherController.getInstance(false);
                client.getChosenUi().start();
            }
            default -> System.out.println("Invalid input");
        }

    }

    public static void startGUI() {
        Client client = new Client(new GUI());
        DispatcherController.getInstance(true);
        client.getChosenUi().start();
    }
}