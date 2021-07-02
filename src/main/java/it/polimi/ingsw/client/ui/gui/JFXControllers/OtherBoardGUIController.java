package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.GUIMessageHandler;

/**
 * Handles the board of other players
 */
public class OtherBoardGUIController extends BoardGUIController {


    public void screenStart() {

        GUIMessageHandler.getInstance().setCurrentGameController(this);

        setPlayerView(GUIHelper.getInstance().getSelectedPlayer());
        GameTemplate.getInstance().setTabs(ScreenName.OTHER_BOARD);

        updateFaithTrack();
        updateDepot();
    }

    public void updateProductions() { }

}
