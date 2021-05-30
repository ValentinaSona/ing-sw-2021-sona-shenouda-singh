package it.polimi.ingsw.utils.networking.transmittables.servermessages;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.DevelopmentCard;
import it.polimi.ingsw.utils.networking.ClientHandleable;

public class ServerBuyDevelopmentCardMessage implements ServerMessage, ClientHandleable {
    private final DevelopmentCard card;
    private final User user;

    public ServerBuyDevelopmentCardMessage(DevelopmentCard card, User user){
        this.card = card;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public DevelopmentCard getCard() {
        return card;
    }

    //TODO

    @Override
    public boolean handleMessage(Client handler) {
        return false;
    }
}
