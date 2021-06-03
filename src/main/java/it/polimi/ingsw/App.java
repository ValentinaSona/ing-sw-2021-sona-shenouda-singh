package it.polimi.ingsw;


import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.ui.cli.CLI;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.client.ui.gui.GUI;
import it.polimi.ingsw.server.Server;
import javafx.application.Application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static void main( String[] args ) throws IOException {
        LOGGER.log(Level.INFO, "Logger initialized");
        switch (args[0]) {
            case "gui" -> {
                Client client = new Client(new GUI());
                //mi metto semplicemente ad aspettare che vengano messi messaggi nella coda
                //per dire al uiController di processarli
                DispatcherController.getInstance(true);
                client.getChosenUi().start();
            }
            case "server" -> new Server(10001).start();
            case "cli" -> {
                Client client = new Client(new CLI());
                //mi metto semplicemente ad aspettare che vengano messi messaggi nella coda
                //per dire al uiController di processarli
                DispatcherController.getInstance(false);
                client.getChosenUi().start();
            }
            default -> System.out.println("Input non valido");
        }

    }
}