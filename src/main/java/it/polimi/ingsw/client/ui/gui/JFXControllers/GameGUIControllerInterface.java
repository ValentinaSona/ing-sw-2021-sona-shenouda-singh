package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerStartTurnMessage;
import javafx.event.ActionEvent;

public interface GameGUIControllerInterface {

    void goToMarket();
    void goToClientBoard();
    void goToDev();
    void goToOtherBoard(ActionEvent e);

    void update();
}
