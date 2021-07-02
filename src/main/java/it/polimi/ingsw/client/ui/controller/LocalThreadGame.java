package it.polimi.ingsw.client.ui.controller;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.controller.User;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.view.MockRemoteViewHandler;
import it.polimi.ingsw.server.view.RemoteViewHandler;
import it.polimi.ingsw.utils.networking.transmittables.resilienza.DisconnectionMessage;

public class LocalThreadGame extends Thread{
    private MockRemoteViewHandler view;
    private boolean active;
    private final Object activeLock = new Object();

    @Override
    public void run(){
        Game model = Game.getInstance(1);

        Controller controller = Controller.getInstance(model);
        String nickname = view.getUser().getNickName();
        User user = new User(nickname);

        model.subscribeUser(user);

        view = new MockRemoteViewHandler(nickname,
                DispatcherController.getInstance());

        model.addObserver(view, (observer, transmittable)->{
            if(transmittable instanceof DisconnectionMessage){
                ((RemoteViewHandler) observer).requestDisconnection();
                setActive(false);
            }else {
                ((RemoteViewHandler) observer).updateFromGame(transmittable);
            }
        });


        view.addObserver(controller, (observer, viewClientMessage) ->
                ((Controller) observer).update(viewClientMessage) );

        controller.setup();



        while (isActive()){
            try{
                controller.dispatchViewClientMessage();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public MockRemoteViewHandler getView() {
        return view;
    }

    public void setView(MockRemoteViewHandler view) {
        this.view = view;
    }

    public boolean isActive() {
        synchronized (activeLock){
            return active;
        }
    }

    public void setActive(boolean active) {
        synchronized (activeLock){
            this.active = active;
        }
    }

}
