package it.polimi.ingsw.utils.observer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LambdaObservable <T>{
    /**
     * This map contains a list of LambdaObserver and a lambda function that represent the update(T message)
     * method to call
     */
    private final Map<LambdaObserver, BiConsumer<LambdaObserver, T>> observerMap = new HashMap<>();

    /**
     * This method can be used to register an observer on the current object
     * @param observer to be registered
     * @param lambda function to call instead of the update(T message) of the observer
     */
    public void addObserver(LambdaObserver observer, BiConsumer<LambdaObserver, T> lambda){
        synchronized (observerMap){
            observerMap.put(observer, lambda);
        }
    }

    public void removeObserver(LambdaObserver observer){
        synchronized (observerMap){
            observerMap.remove(observer);
        }
    }

    /**
     *  for every observer i am calling their update method that is
     *  defined by the associated lambda function
     * @param message message to send to the observers
     */
    public void notify(T message){
        synchronized (observerMap){
            for(LambdaObserver observer : observerMap.keySet()){
                observerMap.get(observer).accept(observer, message);
            }
        }
    }
}
