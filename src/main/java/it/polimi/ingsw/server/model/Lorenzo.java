package it.polimi.ingsw.server.model;


import java.util.Collections;
import java.util.Stack;

public class Lorenzo {

    private Stack<SoloAction> actions;
    private final FaithTrack blackCross;

    public Lorenzo() {
        shuffle();
        blackCross = new FaithTrack();
    }

    public void shuffle(){
        actions = new Stack<>();

        actions.add(SoloAction.MOVE);
        actions.add(SoloAction.MOVE);
        actions.add(SoloAction.MOVE_SHUFFLE);
        actions.add(SoloAction.DISCARD_BLUE);
        actions.add(SoloAction.DISCARD_PURPLE);
        actions.add(SoloAction.DISCARD_GREEN);
        actions.add(SoloAction.DISCARD_YELLOW);

        Collections.shuffle(actions);

    }

    public SoloAction pop(){
        return actions.pop();
    }

    public FaithTrack getFaithTrack() {
        return blackCross;
    }

    public int getBlackCross(){
        return blackCross.getFaithMarker();
    }
}
