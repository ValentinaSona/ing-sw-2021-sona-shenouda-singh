package it.polimi.ingsw;


import it.polimi.ingsw.client.Client;
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
        switch (args[0]){
            case "gui":
                new Client(new GUI()).getChosenUi().start();
                break;
            case "server":
                new Server(9000).start();
                break;
            default:
                System.out.println("Input non valido");
        }

    }
}