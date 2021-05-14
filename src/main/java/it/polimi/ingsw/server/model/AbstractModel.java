package it.polimi.ingsw.server.model;

import it.polimi.ingsw.utils.observer.LambdaObservable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel /*implements LambdaObservable<T>*/ {



    public AbstractModel(){

    }

    public void addListener(PropertyChangeListener listener){
      //  propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener){
    //    propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Every time a model class is modified, this method is called passing:
     * @param propertyName a String that identify the Event
     * @param oldValue the OldValue of the model class or null
     * @param newValue the newValue of the model class that is passed to the view
     */
    protected void update(String propertyName, Object oldValue, Object newValue){
     //   propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
