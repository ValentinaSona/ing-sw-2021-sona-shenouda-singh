package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;

/**
 * This should be a universal controller called by the ui
 */

public class UIController {

    private static UIController singleton;

    public static UIController getInstance() {
        if (singleton == null) singleton = new UIController();
        return singleton;
    }

    public boolean sendNickname(String nickname) {
        return true;
    }

    public boolean joinLobby() {
        return true;
    }
}
