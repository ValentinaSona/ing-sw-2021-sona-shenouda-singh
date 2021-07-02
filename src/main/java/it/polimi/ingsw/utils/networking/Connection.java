package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.utils.networking.transmittables.resilienza.KeepAlive;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;
import it.polimi.ingsw.utils.observer.LambdaObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends LambdaObservable<Transmittable> implements Runnable {
    private final Logger LOGGER = Logger.getLogger(Connection.class.getName());
    /**
     * Socket managed by the connection
     */
    private final Socket socket;
    /**
     * InputStream for receiving messages
     */
    private final ObjectInputStream inputStream;
    /**
     * OutputStream for sending messages
     */
    private final ObjectOutputStream outputStream;

    private boolean active = true;

    private final Object activeLock = new Object();

    /**
     * Constructor of a connection opens the streams
     * @param socket socket to be observed
     * @throws IOException if the opening of the input and output streams fails
     */
    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.outputStream.flush();  //required otherwise the following instantiation of ObjectInputStream will block forever
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public boolean isActive() {
        synchronized (activeLock){
            return active;
        }
    }

    /**
     * if the connection is not closed
     * we try to close this socket
     */
    public void closeConnection(){
        if (!isActive()) {
            return;
        }
        LOGGER.log(Level.INFO, "Closing the connection");
        try {
            outputStream.close();
        } catch (IOException ignored) {
            LOGGER.log(Level.INFO, "Unable to close out socket");
        }
        try {
            inputStream.close();
        } catch (IOException ignored) {
            LOGGER.log(Level.INFO, "Unable to close in socket");
        }
        try {
            socket.close();
        } catch (IOException ignored) {
            LOGGER.log(Level.INFO, "Unable to close socket");
        }
    }

    /**
     * Close the connection and notifies the observer of the connection
     */
    private void notifyDisconnection() {
        if (isActive()) {
            setActive(false);
            notify( new DisconnectionMessage());
        }
    }

    /**
     * Send a message to the remote client
     * @param message this message will be handled by the client and so it has to implement the ClientHandleable interface
     */
    public void send(Transmittable message){
        try{
            //more observers can call the method send from different threads
            synchronized (outputStream){
                if(!isActive()){
                    return;
                }
                outputStream.writeObject(message);
                LOGGER.log(Level.INFO, "Sending message "+ message.getClass().getName()+"...");
                outputStream.flush();
                outputStream.reset();
            }
        }catch (IOException e){
            // The connection has been lost
            notifyDisconnection();
        }
    }

    /**
     * Called when a new thread is started, calls scheduleKeepAliveTimer()  and then waits for incoming messages
     */
    @Override
    public synchronized void run(){

        scheduleKeepAliveTimer();
        while (isActive()){
            try {
                Transmittable inputObject = (Transmittable) inputStream.readObject();
                if (inputObject instanceof KeepAlive) {
                    LOGGER.log(Level.INFO, "Received keep alive");
                }
                else {
                    LOGGER.log(Level.INFO, "Received message "+inputObject.getClass().getName()+"....");
                    notify(inputObject);
                }
            } catch (IOException e) {
                notifyDisconnection();
            } catch (ClassNotFoundException | ClassCastException e) {
                LOGGER.log(Level.INFO, "Exception in receive thread"+e);
            }
        }
    }

    /**
     * Called in the run method creates a Timer thread that manages the sending of
     * keepAlive messages
     */
    public void scheduleKeepAliveTimer(){

        Timer keepAliveTimer = new Timer("KeepAliveTimer");
        final int INTERVAL_MS = 180_000;
        keepAliveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isActive())
                    send(new KeepAlive());
                else
                    cancel();
            }
        },INTERVAL_MS, INTERVAL_MS);
    }

    private void setActive(boolean active) {
        synchronized (activeLock){
            this.active = active;
        }
    }
}
