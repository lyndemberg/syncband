package com.mesh.syncband.data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Profile {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String nickName;
    private String function;

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", function='" + function + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Ignore
    public Profile(String nickName, String function) {

        this.nickName = nickName;
        this.function = function;
    }

    @Ignore
    public Profile(int id, String nickName, String function) {

        this.id = id;
        this.nickName = nickName;
        this.function = function;
    }

    public Profile() {

    }
}
