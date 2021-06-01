package it.polimi.ingsw.server.controller;

import java.io.Serializable;

public class User implements Serializable {
    public String nickName;

    public User(String nickName){
        this.nickName = nickName;
    }

    public String getNickName(){
        return nickName;
    }
}
