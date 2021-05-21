package it.polimi.ingsw;


import it.polimi.ingsw.client.ui.gui.GUI;
import javafx.application.Application;

/**
 * Hello world!
 *
 */
public class App 
{

    static String banner = "" +
            " __  __           _                         __   _   _            _____                  _                              \n" +
            "|  \\/  |         | |                       / _| | | | |          |  __ \\                (_)                             \n" +
            "| \\  / | __ _ ___| |_ ___ _ __ ___    ___ | |_  | |_| |__   ___  | |__) |___ _ __   __ _ _ ___ ___  __ _ _ __   ___ ___ \n" +
            "| |\\/| |/ _` / __| __/ _ \\ '__/ __|  / _ \\|  _| | __| '_ \\ / _ \\ |  _  // _ \\ '_ \\ / _` | / __/ __|/ _` | '_ \\ / __/ _ \\\n" +
            "| |  | | (_| \\__ \\ ||  __/ |  \\__ \\ | (_) | |   | |_| | | |  __/ | | \\ \\  __/ | | | (_| | \\__ \\__ \\ (_| | | | | (_|  __/\n" +
            "|_|  |_|\\__,_|___/\\__\\___|_|  |___/  \\___/|_|    \\__|_| |_|\\___| |_|  \\_\\___|_| |_|\\__,_|_|___/___/\\__,_|_| |_|\\___\\___|\n" +
            "                                                                                                                        ";
    public static void main( String[] args )
    {
        System.out.println( banner );
        Application.launch(GUI.class);
    }
}