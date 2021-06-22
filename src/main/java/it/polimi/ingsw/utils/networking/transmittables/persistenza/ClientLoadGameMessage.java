package it.polimi.ingsw.utils.networking.transmittables.persistenza;

import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientLoadGameMessage implements ClientMessage, ServerHandleable {
    private final String nickname;

    public ClientLoadGameMessage(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }

    @Override
    public boolean handleMessage(ConnectionSetupHandler handler){
        Lobby lobby = handler.getLobby();
        Connection connection = handler.getConnection();

        boolean status = lobby.handleNicknameRegistration(nickname, connection, true);

        if(status){
            //tell the client that the operation ended successfully
            connection.send(StatusMessage.LOAD_GAME_OK);
            handler.setNickname(nickname);
        }else {
            connection.send(StatusMessage.CLIENT_ERROR);
        }

        return status;
    }
}
