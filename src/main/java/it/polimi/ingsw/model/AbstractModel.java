package it.polimi.ingsw.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractModel {
    public static final String IS_NOT_YOUR_TURN ="is_not_your_turn";
    public static final String DEPOT_UPDATE = "depot_update";
    public static final String DEPOT_TYPE_ERROR = "depot_type_error";
    public static final String DEPOT_QUANTITY_ERROR = "depot_quantity_error";
    public static final String ILLEGAL_ACTION = "illegal_action";
    public static final String TEMP_RESOURCES_EMPTY = "temp_resources_empty";
    public static final String ACTION_USED = "action_used";
    public static final String TEMP_RESOURCES_UPDATE = "temp_resources_update";
    public static final String WAREHOUSE_TYPE_ERROR = "warehouse_type_error";
    public static final String DEVEL_CARD_LEVEL_ERROR = "development_card_level_error";
    public static final String TARGET_CARD = "target_card";
    public static final String LEADERCARD_REQUIREMENTS = "leadercard_requirements";
    public static final String JOLLY_RESOURCE = "jolly_resource";
    protected PropertyChangeSupport propertyChangeSupport;


    public AbstractModel(){
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void addListener(PropertyChangeListener listener){
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener){
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Every time a model class is modified, this method is called passing:
     * @param propertyName a String that identify the Event
     * @param oldValue the OldValue of the model class or null
     * @param newValue the newValue of the model class that is passed to the view
     */
    protected void update(String propertyName, Object oldValue, Object newValue){
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
