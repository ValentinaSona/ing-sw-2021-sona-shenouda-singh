package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.mockito.Mockito.*;

public class LobbyTest {
    private Lobby lobby;
    private Connection[] mockConnections;
    private ConnectionSetupHandler[] connectionHandlers;
    private BlockingQueue<Transmittable> backToClient;
    private Thread lobbyThread;


    @BeforeEach
    public void initLobby(){
       Server mockServer = mock(Server.class);
       doAnswer((InvocationOnMock invocation)-> {
           return null;
       }).when(mockServer).submitMatch(any(Match.class));

       lobby = Lobby.getInstance(mockServer);
       backToClient = new LinkedBlockingDeque<>();
       lobbyThread = new Thread(()-> lobby.start());
       lobbyThread.start();
    }

    @AfterEach
    public void closeLobby(){
        lobby.stop();
    }

    public void mockConnections(int numberOfConnections){
        mockConnections = new Connection[numberOfConnections];
        connectionHandlers = new ConnectionSetupHandler[numberOfConnections];

        for(int i= 0; i < numberOfConnections; i++){
            Connection mockConnection = mock(Connection.class);
            ConnectionSetupHandler handler = new ConnectionSetupHandler(lobby, mockConnection);
            connectionHandlers[i] = handler;
            mockConnections[i] = mockConnection;
            doAnswer((InvocationOnMock invocation)  ->{
                        backToClient.put(invocation.getArgument(0));
                        return null;
                    }
            ).when(mockConnection).send(any(Transmittable.class));
            //saying what action should be performed if the mockConnection send a transmittable to the server
            //we don't want to test the server so we are using a mock connection
        }
    }

    public void setNickname(int idx, String nickname) throws InterruptedException {
        connectionHandlers[idx].update(new ClientSetNicknameMessage(nickname));
        assertEquals(StatusMessage.OK, backToClient.take());
    }

    public void setInvalidNickname(int idx, String nickname)throws InterruptedException {
        connectionHandlers[idx].update(new ClientSetNicknameMessage(nickname));
        assertEquals(StatusMessage.CLIENT_ERROR, backToClient.take());
    }

    public void setJoinLobby(int idx, boolean countYetToSet, int count) throws InterruptedException {
        Thread t = new Thread(()->{
            connectionHandlers[idx].update(new ClientJoinLobbyMessage());
            if(countYetToSet){
                //if i am the first player to join
                Transmittable statusContinue = null;
                try {
                    statusContinue = backToClient.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                assertEquals(StatusMessage.CONTINUE, statusContinue);
                connectionHandlers[idx].update(new ClientSetPlayersCountMessage(count));
            }
        });
        t.start();
        t.join();

    }

    @Test
    @DisplayName("Set the nickname for a single player")
    public void setValidNicknameForRegistration() throws InterruptedException {
        mockConnections(1);
        //this is the first client to set the nickname so it should be available and
        //so the message back to the client should be an ok message
        setNickname(0, "Ugo");
    }

    @Test
    @DisplayName("Four players trying to set the same nickname from different threads")
    public void setInvalidNicknamesForRegistration() throws InterruptedException {
        mockConnections(4);

        setNickname(0, "luca");
        setInvalidNickname(1,"luca");
        setInvalidNickname(2,"luca");
        setInvalidNickname(3,"luca");
    }

    @Test
    @DisplayName("Four players set four different nicknames")
    public void setDifferentNicknames() throws InterruptedException {
        mockConnections(4);

        setNickname(0,"Gianni");
        setNickname(1,"Paolo");
        setNickname(2,"Giovanni");
        setNickname(3,"Arnaldo");
    }

    @Test
    @DisplayName("Player set his name and player count")
    public void joinLobbyAndSetPlayerCount() throws InterruptedException {
        mockConnections(3);

        setNickname(0, "Gianni");
        setJoinLobby(0,true, 4);

        setNickname(1, "Giorgio");
        setJoinLobby(1,false, 4);

        setNickname(2, "Giovanni");
        setJoinLobby(2,false, 4);

        assertEquals(4, lobby.getCurrentLobbyPlayerCount());
    }

    @Test
    @DisplayName("All requested players join the lobby and a match is created")
    public void createMatch() throws InterruptedException {
        mockConnections(4);


        setNickname(0, "Gianni");
        setJoinLobby(0,true, 4);

        setNickname(1, "Giorgio");
        setJoinLobby(1,false, 4);

        setNickname(2, "Giovanni");
        setJoinLobby(2,false, 4);

        setNickname(3, "Gino");
        setJoinLobby(3,false, 4);

        lobbyThread.join();
        assertEquals(4, lobby.getCurrentLobbyPlayerCount());
        assertFalse(lobby.isActive());
        assertFalse(lobbyThread.isAlive());

    }
}
