package com.mesh.syncband.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mesh.syncband.data.Song;

import java.util.List;

@Dao
public interface SongDao {
    @Query("SELECT * FROM song WHERE idSetlist=:idSetlist")
    List<Song> findAllBySetlist(int idSetlist);
    @Insert
    void save(Song song);
    @Delete
    void delete(Song song);
    @Update
    void update(Song updated);
}
