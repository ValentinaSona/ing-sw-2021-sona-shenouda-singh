package it.polimi.ingsw;


import it.polimi.ingsw.client.ui.gui.GUI;
import javafx.application.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    public static void main( String[] args )
    {
        LOGGER.log(Level.INFO, "Logger initialized");
        Application.launch(GUI.class);
    }
}