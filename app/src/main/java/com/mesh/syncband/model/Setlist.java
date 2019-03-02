package com.mesh.syncband.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;


@Entity(indices = {
    @Index(value = "name",unique = true)
})
public class Setlist {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Date created;
    private Date lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Setlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Ignore
    public Setlist(String name, Date created, Date lastUpdated) {

        this.name = name;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public Setlist() {

    }
}
