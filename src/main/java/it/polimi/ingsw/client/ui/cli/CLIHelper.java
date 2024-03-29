package it.polimi.ingsw.client.ui.cli;

/**
 * Contains all the unicode ANSI and special strings used throughout the CLI.
 */

public class CLIHelper {
    public static final String ANSI_RED = "\033[31m";
    public static final String ANSI_BLACK = "\033[030m";
    public static final String ANSI_GREEN = "\033[32m";
    public static final String ANSI_YELLOW = "\033[33m";
    public static final String ANSI_BLUE = "\033[34m";
    public static final String ANSI_PURPLE = "\033[35m";
    public static final String ANSI_WHITE = "\033[37m";
    public static final String ANSI_RESET = "\033[0m";
    public static final String CHECK_MARK = "\u2713";
    public static final String CIRCLE = "\u25CF";
    public static final String WHITE_CIRCLE = "\u25CB";
    public static final String SQUARE = "\u25A0";
    public static final String CROSS = "\u2671";

    public static final String MARBLE_LEGEND =
                ANSI_BLUE + CIRCLE + ANSI_RESET + " Blue, "+
                ANSI_YELLOW + CIRCLE + ANSI_RESET + " Yellow, "+
                ANSI_PURPLE + CIRCLE + ANSI_RESET + " Purple, "+
                ANSI_WHITE+ CIRCLE + ANSI_RESET + " Gray, "+
                ANSI_RED + CIRCLE + ANSI_RESET + " Red, " +
                WHITE_CIRCLE +  " White";

    public static final String FAITH_TRACK =
                ANSI_WHITE + " " + SQUARE +  " " + SQUARE +  " " + SQUARE +  " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE +  " " + SQUARE +  " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE + " " + ANSI_RESET + ANSI_RED + SQUARE + " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE + " " + SQUARE + " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE +  " " + SQUARE + " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_RED + SQUARE + " " + ANSI_RESET + ANSI_WHITE + SQUARE + " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE + " " + SQUARE + " " + ANSI_RESET + ANSI_YELLOW + SQUARE + " " + ANSI_RESET +
                ANSI_WHITE + SQUARE + " " + SQUARE + " " + ANSI_RESET + ANSI_RED + SQUARE + " " + ANSI_RESET + "\n" +
                "          |-------|     |---------|   |-----------|\n";




}

