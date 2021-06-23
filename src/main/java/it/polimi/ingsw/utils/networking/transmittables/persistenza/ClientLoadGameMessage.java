package it.polimi.ingsw.utils.networking.transmittables.persistenza;

import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientLoadGameMessage implements ClientMessage, ServerHandleable {

    @Override
    public boolean handleMessage(ConnectionSetupHandler handler){
        Lobby lobby = handler.getLobby();
        Connection connection = handler.getConnection();

        boolean status = handler.getLobby().handleResumeGame(connection);

        if(status){
            //tell the client that the operation ended successfully
            connection.send(StatusMessage.OK_COUNT);
        }else {
            connection.send(StatusMessage.CLIENT_ERROR);
        }

        return status;
    }
}
