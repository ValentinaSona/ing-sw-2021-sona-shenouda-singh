package it.polimi.ingsw.utils.networking.transmittables.clientmessages.game;

import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientGameReconnectionMessage implements ClientMessage, ServerHandleable {
    private final String nickname;

    public ClientGameReconnectionMessage(String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean handleMessage(ConnectionSetupHandler handler) {
        Lobby lobby = handler.getLobby();
        Connection connection = handler.getConnection();
        handler.setNickname(nickname);

        //now i try to join the lobby with the following nickname and connection
        boolean status = lobby.handleGameReconnection(nickname, connection);

        return status;
    }
}
