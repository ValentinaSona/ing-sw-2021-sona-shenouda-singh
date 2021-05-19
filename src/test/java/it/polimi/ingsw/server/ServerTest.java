package it.polimi.ingsw.server;

import org.junit.jupiter.api.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

public class ServerTest {
    Server server = null;




    @BeforeEach
    public void initServer(){
        try {
            server = new Server(20000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @AfterEach
    public void closeServer(){
        server.shutDown();
    }

    @Test
    public void startCloseServer(){
        Thread t = new Thread(()->server.start());
        t.start();
        server.shutDown();
        //waiting until the server is closed and the thread is done running
        await().until(()->!t.isAlive());
        assertFalse(t.isAlive());
    }
}
