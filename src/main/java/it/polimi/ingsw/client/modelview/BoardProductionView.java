package it.polimi.ingsw.client.modelview;

import it.polimi.ingsw.server.model.Id;

/**
 * Board production, always turns two jolly into one jolly.
 */
public class BoardProductionView extends SlotView {
    public BoardProductionView(Id boardProduction) {
        super(boardProduction);
    }
}
