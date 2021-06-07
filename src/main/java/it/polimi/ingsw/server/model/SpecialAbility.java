package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class SpecialAbility implements Serializable {

    private String abilityType;

    public void setAbilityType(String abilityType) {
        this.abilityType = abilityType;
    }

    public String getAbilityType() {
        return abilityType;
    }
}
