package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.DevelopmentCardSlotView;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.ProductionState;

import java.util.ArrayList;
import java.util.List;

public class SelectedProductions {

    private static SelectedProductions singleton;

    private List<ProductionState> productions;

    public static SelectedProductions getInstance() {
        if (singleton == null) singleton = new SelectedProductions();
        return singleton;
    }

    private SelectedProductions() {
        update();
    }

    public void update() {
        var slots = GUIHelper.getInstance().getClientView().getSlots();
        productions  = new ArrayList<>();

        productions.add(ProductionState.IDLE);

        for (int i = 1; i < 4; i++) {
            if (((DevelopmentCardSlotView)slots.get(i)).peek() != null) productions.add(ProductionState.IDLE);
            else productions.add(ProductionState.EMPTY);
        }

        productions.add(ProductionState.EMPTY);
        productions.add(ProductionState.EMPTY);
    }

    public void set(int index, ProductionState state) {
        productions.set(index, state);
    }

    public ProductionState get(int index) {
        return productions.get(index);
    }

    public List<ProductionState> getProductions() {
        return productions;
    }

    public void confirm() {
        for (var p : productions) {
            if(p == ProductionState.SELECTED) {
                productions.set(productions.indexOf(p), ProductionState.CONFIRMED);
            }
        }
    }

    public void reset() {
        for (var p : productions) {
            if(p != ProductionState.EMPTY && p != ProductionState.IDLE) {
                productions.set(productions.indexOf(p), ProductionState.IDLE);
            }
        }
    }
}
