package it.polimi.ingsw.server.controller;

public class User {
    public String nickName;

    public User(String nickName){
        this.nickName = nickName;
    }

    public String getNickName(){
        return nickName;
    }
}
