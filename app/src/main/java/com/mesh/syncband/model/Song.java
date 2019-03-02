package com.mesh.syncband.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = {
    @ForeignKey(
        entity = Setlist.class,
        parentColumns = "id",
        childColumns = "idSetlist",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
}
)
public class Song {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int idSetlist;

    private String name;
    private String artist;
    private int bpm;

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", idSetlist=" + idSetlist +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", bpm=" + bpm +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdSetlist() {
        return idSetlist;
    }

    public void setIdSetlist(int idSetlist) {
        this.idSetlist = idSetlist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    @Ignore
    public Song(String name, String artist, int bpm) {
        this.name = name;
        this.artist = artist;
        this.bpm = bpm;
    }

    public Song() {

    }
}
