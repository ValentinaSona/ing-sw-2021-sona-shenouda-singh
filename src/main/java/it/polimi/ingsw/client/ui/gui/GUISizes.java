package it.polimi.ingsw.client.ui.gui;

public class GUISizes {

    private static GUISizes singleton;

    private int devSize;
    private int devSizeY;

    private int marbles;

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

}
