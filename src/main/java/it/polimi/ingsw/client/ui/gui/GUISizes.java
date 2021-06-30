package it.polimi.ingsw.client.ui.gui;

public class GUISizes {

    private static GUISizes singleton;

    private int devSize;
    private int devSizeY;

    private int devOffset;

    private int leaderShowX;
    private int leaderShowY;

    private int abilityX;
    private int abilityY;

    private int popeTile;

    private int marbles;

    private int strongboxCols;

    private int strongBoxRes;
    private int depotRes;

    private int logX;
    private int logY;
    private int logSpacing;

    private int faithTrack;

    private int chosenMarbles;
    private int abilityMarbles;

    public static GUISizes get() {
        if (singleton == null) singleton = new GUISizes();
        return  singleton;
    }

    private GUISizes() {
        if (GUIHelper.getInstance().getResolution() > 1000 ) setNormalScreen();
    }

    private void setNormalScreen() {

        devSize = 187;
        devSizeY = 279;

        devOffset = 120;

        marbles = 110;

        leaderShowX = 369;
        leaderShowY = 558;

        abilityX = 200;
        abilityY = 71;

        popeTile = 90;

        strongboxCols = 6;

        strongBoxRes = 50;
        depotRes = 62;

        logX = 400;
        logY = 350;
        logSpacing = 8;

        faithTrack = 62;

        chosenMarbles = 130;
        abilityMarbles = 90;

    }

    public int devSize() {
        return devSize;
    }

    public int marbles() {
        return  marbles;
    }

    public int devSizeY() {
        return devSizeY;
    }

    public int leaderShowX() {return leaderShowX;}
    public int leaderShowY() {return leaderShowY;}

    public int abilityX() {return abilityX;}
    public int abilityY() {return abilityY;}

    public int popeTile() {return popeTile;}

    public int strongboxCols() {return strongboxCols;}

    public int strongboxRes() {return strongBoxRes;}

    public int logX() { return logX; }

    public int logY() { return logY; }

    public int logSpacing() { return logSpacing; }

    public int faithTrack() { return faithTrack; }

    public int devOffset() { return devOffset; }

    public int depotRes() {
        return depotRes;
    }

    public int chosenMarbles() { return  chosenMarbles; }
    public int abilityMarbles() { return abilityMarbles; }
}
