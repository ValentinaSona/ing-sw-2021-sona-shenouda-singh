package it.polimi.ingsw.server.controller;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    public String nickName;

    public User(String nickName){
        this.nickName = nickName;
    }

    public String getNickName(){
        return nickName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(nickName, user.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickName);
    }
}
