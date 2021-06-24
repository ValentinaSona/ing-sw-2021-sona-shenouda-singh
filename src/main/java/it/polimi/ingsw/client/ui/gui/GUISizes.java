package it.polimi.ingsw.client.ui.gui;

public class GUISizes {

    private static GUISizes singleton;

    private int devSize;
    private int devSizeY;

    private int leaderShowX;
    private int leaderShowY;

    private int abilityX;
    private int abilityY;

    private int popeTile;

    private int marbles;

    private int strongboxCols;

    private int strongBoxRes;

    public static GUISizes get() {
        if (singleton == null) singleton = new GUISizes();
        return  singleton;
    }

    private GUISizes() {
        if (GUIHelper.getInstance().getResolution() > 992 ) setNormalScreen();
    }

    private void setNormalScreen() {

        devSize = 187;
        devSizeY = 279;

        marbles = 110;

        leaderShowX = 369;
        leaderShowY = 558;

        abilityX = 221;
        abilityY = 84;

        popeTile = 90;

        strongboxCols = 6;

        strongBoxRes = 50;

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

}
