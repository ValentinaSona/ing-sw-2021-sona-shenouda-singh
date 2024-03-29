package it.polimi.ingsw.server;

import it.polimi.ingsw.utils.networking.Connection;
import it.polimi.ingsw.utils.networking.Transmittable;
import it.polimi.ingsw.utils.networking.transmittables.StatusMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientJoinLobbyMessage;
import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetNicknameMessage;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.utils.networking.transmittables.clientmessages.setup.ClientSetPlayersCountMessage;
import it.polimi.ingsw.utils.networking.transmittables.servermessages.ServerUpdateLobbyMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static org.mockito.Mockito.*;

class LobbyTest {
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

        assertEquals(StatusMessage.OK_NICK, backToClient.take());
    }

    public void setInvalidNickname(int idx, String nickname)throws InterruptedException {
        connectionHandlers[idx].update(new ClientSetNicknameMessage(nickname));
        assertEquals(StatusMessage.CLIENT_ERROR, backToClient.take());
    }

    public void setJoinLobby(int idx, boolean countYetToSet, int count) throws InterruptedException {
        Thread t = new Thread(()->{
            connectionHandlers[idx].update(new ClientJoinLobbyMessage());
            Transmittable statusContinue = null;

            try{
                statusContinue = backToClient.take();
                if(countYetToSet){
                    //if i am the first player to join
                    assertEquals(StatusMessage.SET_COUNT, statusContinue);
                    connectionHandlers[idx].update(new ClientSetPlayersCountMessage(count));
                    statusContinue = backToClient.take();
                    assertEquals(StatusMessage.OK_COUNT, statusContinue);
                }else{
                    assertEquals(StatusMessage.JOIN_LOBBY, statusContinue);

                    for(int i = 0; i<=idx; i++){
                        statusContinue = backToClient.take();
                        assertEquals(count, ((ServerUpdateLobbyMessage)statusContinue).getNumOfPlayer());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        t.start();
        t.join();

    }

    @Test
    @DisplayName("Set the nickname for a single player")
    void setValidNicknameForRegistration() throws InterruptedException {
        mockConnections(1);
        //this is the first client to set the nickname so it should be available and
        //so the message back to the client should be an ok message
        setNickname(0, "Ugo");
    }

    @Test
    @DisplayName("Four players trying to set the same nickname from different threads")
    void setInvalidNicknamesForRegistration() throws InterruptedException {
        mockConnections(4);

        setNickname(0, "luca");
        setInvalidNickname(1,"luca");
        setInvalidNickname(2,"luca");
        setInvalidNickname(3,"luca");
    }

    @Test
    @DisplayName("Four players set four different nicknames")
    void setDifferentNicknames() throws InterruptedException {
        mockConnections(4);

        setNickname(0,"Gianni");
        setNickname(1,"Paolo");
        setNickname(2,"Giovanni");
        setNickname(3,"Arnaldo");
    }

    @Test
    @DisplayName("Player set his name and player count")
    void joinLobbyAndSetPlayerCount() throws InterruptedException {
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
    void createMatch() throws InterruptedException {
        mockConnections(4);


        setNickname(0, "Gianni");
        setJoinLobby(0,true, 4);

        setNickname(1, "Giorgio");
        setJoinLobby(1,false, 4);

        setNickname(2, "Giovanni");
        setJoinLobby(2,false, 4);

        setNickname(3, "Gino");
        setJoinLobby(3,false, 4);


        assertEquals(4, lobby.getCurrentLobbyPlayerCount());
        assertTrue(lobby.isActive());
        assertTrue(lobbyThread.isAlive());

    }
}
