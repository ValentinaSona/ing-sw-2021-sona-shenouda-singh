package it.polimi.ingsw.server.model;

import java.io.Serializable;

public class Requirement implements Serializable {

    final private int number;
    final private int level;
    final private DevelopmentType type;

    final private Resource resource;
    final private boolean isResource;

    public Requirement(int number, int level, DevelopmentType type) {
        this.number = number;
        this.level = level;
        this.type = type;
        this.isResource = false;
        this.resource = null;
    }
    public Requirement(Resource resource) {
        this.number = 0;
        this.level = 0;
        this.type = null;
        this.isResource = true;
        this.resource = resource;
    }



    public int getNumber() {
        return number;
    }

    public int getLevel() {
        return level;
    }

    public DevelopmentType getType() {
        return type;
    }

    public Resource getResource() {
        return resource;
    }

    public boolean isResource() {
        return isResource;
    }

    @Override
    public String toString() {
        if (!isResource){
            String levels = "";
            if (level !=0)  levels = "(Lv. " + level +")";
            return number + " " + type + levels;
        } else {
            assert resource != null;
            return resource.toString();
        }
    }


}
