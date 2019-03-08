package com.mesh.syncband.valueobject;

import java.io.Serializable;

public class SongVo implements Serializable {
    private String name;
    private String artist;
    private Double bpm;
    private String album;
    private String photo;


    public SongVo(String name, String artist, Double bpm, String album, String photo) {
        this.name = name;
        this.artist = artist;
        this.bpm = bpm;
        this.album = album;
        this.photo = photo;
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

    public Double getBpm() {
        return bpm;
    }

    public void setBpm(Double bpm) {
        this.bpm = bpm;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public SongVo() {

    }
}
