package com.mesh.syncband.socket.valueobject;

import java.io.Serializable;
import java.sql.Timestamp;

public class SongStart implements Serializable {
    public String name;
    public String artist;
    public int bpm;
    public Timestamp start;
}
