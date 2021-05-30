package it.polimi.ingsw.client.ui;

import it.polimi.ingsw.utils.networking.Transmittable;

public interface UiControllerInterface {
    //ogni controller saprà gestire una certa tipologia di messaggi e basta
    public abstract void handleMessage(Transmittable message);
}
