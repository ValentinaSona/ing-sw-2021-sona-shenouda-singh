package it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup;

import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

/**
 * ClientMessage--> is the source of the message
 * ServerHandlable--> is the end-point that will process this message
 */
public class ClientJoinLobbyMessage implements ClientMessage, ServerHandleable {

    private final boolean loadFromGame;

    public ClientJoinLobbyMessage(){
        this.loadFromGame = false;
    }

    public ClientJoinLobbyMessage(boolean loadFromGame){
        this.loadFromGame = loadFromGame;
    }

    public boolean isLoadFromGame() {
        return loadFromGame;
    }

    @Override
    public boolean handleMessage(ConnectionSetupHandler handler){
        Lobby lobby = handler.getLobby();
        Connection connection = handler.getConnection();
        String nickname = handler.getNickname();

        //now i try to join the lobby with the following nickname and connection
        boolean status = lobby.handleLobbyJoiningRequest(this, nickname, connection);

        if(!status){
            connection.send(StatusMessage.CLIENT_ERROR);
        }

        return status;
    }
}
