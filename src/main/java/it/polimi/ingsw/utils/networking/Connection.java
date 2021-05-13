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
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.outputStream.flush();  //required otherwise the folowing instantiation of ObjectInptStream will block forever

    }

    public boolean isActive() {
        return active;
    }

    /**
     * if the connection is not closed
     * we try to close this socket
     */
    public void closeConnection(){
        if(active){
            return;
        }
        try{
            outputStream.close();
            inputStream.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the remote client
     * @param message
     */
    public void send(Transmittable message){
        try{
            synchronized (outputStream){
                if(!active){
                    return;
                }
                outputStream.writeObject(message);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        while (isActive()){
            try{
                Transmittable inputObject = (Transmittable) inputStream.readObject();
                this.notify(inputObject);
            }catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
    //TODO: added to remove error, needs to be implemented.
    public void close() {
    }
}
