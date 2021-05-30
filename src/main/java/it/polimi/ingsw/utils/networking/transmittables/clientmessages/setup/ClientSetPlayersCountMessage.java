package it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup;

import it.polimi.ingsw.server.ConnectionSetupHandler;
import it.polimi.ingsw.server.Lobby;
import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.ServerHandleable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.ClientMessage;

public class ClientSetPlayersCountMessage implements ClientMessage, ServerHandleable {
    private final int playersCount;

    public ClientSetPlayersCountMessage(int playersCount){
        super();
        this.playersCount = playersCount;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    @Override
    public boolean handleMessage(ConnectionSetupHandler handler){
        Lobby lobby = handler.getLobby();
        Connection connection = handler.getConnection();

        boolean status = lobby.setLobbyMaxPlayerCount(playersCount, connection);

        //the player is able to set the value
        if(status){
            connection.send(StatusMessage.OK_COUNT);
        }else{
            //player count has already been set
            connection.send(StatusMessage.CLIENT_ERROR);
        }

        return status;
    }
}
