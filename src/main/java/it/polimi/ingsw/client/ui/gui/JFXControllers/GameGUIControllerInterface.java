package it.polimi.ingsw.client.ui.gui.JFXControllers;

import javafx.event.ActionEvent;

/**
 * Interface for all the main controllers used during the actual game
 */
public interface GameGUIControllerInterface {

    void goToMarket();
    void goToClientBoard();
    void goToDev();
    void goToOtherBoard(ActionEvent e);

    void update();
}
