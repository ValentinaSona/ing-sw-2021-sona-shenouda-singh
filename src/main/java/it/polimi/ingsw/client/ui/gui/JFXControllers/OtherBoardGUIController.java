package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.ui.gui.GUIHelper;
public class OtherBoardGUIController extends BoardGUIController {


    public void screenStart() {
        setPlayerView(GUIHelper.getInstance().getSelectedPlayer());
        GameTemplate.getInstance().setTabs(ScreenName.OTHER_BOARD);

        updateFaithTrack();
        updateDepot();
    }

}
