package com.mesh.syncband.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    //access @Entity Song
    @Query("SELECT * FROM song WHERE idSetlist=:idSetlist")
    LiveData<List<Song>> findAllBySetlist(int idSetlist);
    @Insert
    void save(Song song);
    @Delete
    void delete(Song song);
    @Update
    void update(Song updated);
}
