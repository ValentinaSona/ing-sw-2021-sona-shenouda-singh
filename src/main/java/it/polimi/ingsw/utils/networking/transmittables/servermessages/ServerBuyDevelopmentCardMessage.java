package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.modelview.DevMarketView;
import it.polimi.ingsw.client.modelview.SlotView;
import it.polimi.ingsw.client.ui.controller.DispatcherController;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

import java.util.List;

public class ServerBuyDevelopmentCardMessage implements ServerMessage, ClientHandleable {
    private final DevMarketView view;
    private final List<SlotView> slots;
    private final User user;

    public ServerBuyDevelopmentCardMessage(DevMarketView view, List<SlotView> slots,User user){
        this.view = view;
        this.user = user;
        this.slots = slots;
    }

    public User getUser() {
        return user;
    }

    public DevMarketView getView() {
        return view;
    }

    public List<SlotView> getSlots() {
        return slots;
    }

    @Override
    public boolean handleMessage(DispatcherController handler) {
        handler.handleBuyDevelopmentCard(this);
        return false;
    }
}
