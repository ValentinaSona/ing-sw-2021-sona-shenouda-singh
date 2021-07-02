package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.controller.UiControllerInterface;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;
import javafx.scene.Node;

/**
 * Abstract class of JavaFX controllers
 */
public abstract class AbstractGUIController implements UiControllerInterface {

    public AbstractGUIController(){
        GUIMessageHandler.getInstance().setCurrentController(this);
    }

    public void change (ScreenName screen) {
        GUIHelper.getInstance().setScreen(screen);
    }

    public void disableNode(Node node) {
        node.setOpacity(0);
        node.setDisable(true);
    }

    public void enableNode(Node node) {
        node.setOpacity(1);
        node.setDisable(false);
    }

    public void enableNode(Node node, double opacity) {
        node.setOpacity(opacity);
        node.setDisable(false);
    }



}
