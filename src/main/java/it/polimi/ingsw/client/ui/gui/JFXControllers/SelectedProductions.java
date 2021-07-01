package it.polimi.ingsw.client.ui.gui.JFXControllers;

import it.polimi.ingsw.client.modelview.DevelopmentCardSlotView;
import it.polimi.ingsw.client.ui.gui.GUIHelper;
import it.polimi.ingsw.client.ui.gui.ProductionState;
import it.polimi.ingsw.server.model.ProductionAbility;

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
        var slots = GUIHelper.getInstance().getClientView().getSlots();
        productions  = new ArrayList<>();

        productions.add(ProductionState.IDLE);

        for (int i = 1; i < 4; i++) {
            if (((DevelopmentCardSlotView)slots.get(i)).peek() != null) productions.add(ProductionState.IDLE);
            else productions.add(ProductionState.EMPTY);
        }

        var first = GUIHelper.getInstance().getClientView().getLeaderCards().get(0);
        var second = GUIHelper.getInstance().getClientView().getLeaderCards().get(1);

        if (first != null && first.isActive() && first.getSpecialAbility() instanceof ProductionAbility) productions.add(ProductionState.IDLE);
        else productions.add(ProductionState.EMPTY);

        if (second != null && second.isActive() && second.getSpecialAbility() instanceof ProductionAbility) productions.add(ProductionState.IDLE);
        else productions.add(ProductionState.EMPTY);    }

    public void update() {
        var slots = GUIHelper.getInstance().getClientView().getSlots();

        for (int i = 1; i < 4; i++) {
            if (((DevelopmentCardSlotView)slots.get(i)).peek() != null && productions.get(i) == ProductionState.EMPTY) productions.set(i, ProductionState.IDLE);
        }

        var first = GUIHelper.getInstance().getClientView().getLeaderCards().get(0);
        var second = GUIHelper.getInstance().getClientView().getLeaderCards().get(1);

        if (first != null && first.isActive() && first.getSpecialAbility() instanceof ProductionAbility && productions.get(4) == ProductionState.EMPTY)
            productions.set(4, ProductionState.IDLE);

        if (second != null && second.isActive() && second.getSpecialAbility() instanceof ProductionAbility && productions.get(5) == ProductionState.EMPTY)
            productions.set(5, ProductionState.IDLE);


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

    public void adjust() {
        for (var p : productions) {
            if(p == ProductionState.SELECTED) {
                productions.set(productions.indexOf(p), ProductionState.IDLE);
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
