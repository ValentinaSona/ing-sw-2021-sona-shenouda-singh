package it.polimi.ingsw.utils.networking;

import it.polimi.ingsw.utils.observer.LambdaObservable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection extends LambdaObservable<Transmittable> implements Runnable {
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private boolean active = true;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.outputStream.flush();  //required otherwise the following instantiation of ObjectInputStream will block forever
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    public boolean isActive() {
        return active;
    }

    /**
     * if the connection is not closed
     * we try to close this socket
     */
    public synchronized void closeConnection(){
        if(!isActive()){
            //if the connection is already close
            return;
        }
        active = false;
        try{
            outputStream.close();
            inputStream.close();
            socket.close();
        }catch(IOException e){
            //we encountered a problem while closing
            e.printStackTrace();
        }
    }

    //TODO need to understand hot to implement the message because it will be handled -->se also the lobby methods and remoteView
    //in 2 different ways depending if the observer of the connection is the ConnectionSetupHandler
    //or the observers are the RealRemoteViewHandler and the controller
/*    private synchronized void notifyDisconnection(){
        if(active){
            active = false;
            notify(new DisconnectionMessage());
        }
    }

 */
    /**
     * Send a message to the remote client
     * @param message this message will be handled by the client and so it has to implement the ClientHandleable interface
     */
    public void send(Transmittable message){
        try{
            //more observers can call the method send from different threads
            synchronized (outputStream){
                if(!active){
                    return;
                }
                outputStream.writeObject(message);
                System.out.println("Ho mandato un messaggio"+ message);
                outputStream.flush();
                outputStream.reset();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //TODO probably this method is yet to finish
    @Override
    public synchronized void run(){
        while (isActive()){
            try{
                System.out.println("InputStream .."+inputStream.available());
                Transmittable inputObject = (Transmittable) inputStream.readObject();
                System.out.println("Ho ricevuto un messaggio"+ inputObject);
                this.notify(inputObject);
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

}
