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
public interface SetlistDao {

    //access @Entity Setlist
    @Query("SELECT * FROM setlist WHERE id=:id")
    LiveData<Setlist> findById(int id);
    @Query("SELECT * FROM setlist WHERE name=:name")
    LiveData<Setlist> findByName(String name);
    @Query("SELECT * FROM setlist WHERE name=:name")
    Setlist findByNameSync(String name);
    @Query("SELECT * FROM setlist")
    LiveData<List<Setlist>> getAll();
    @Query("SELECT name FROM setlist")
    LiveData<List<String>> getAllNames();
    @Insert
    void save(Setlist setlist);
    @Delete
    void delete(Setlist setlist);
    @Query("DELETE FROM setlist WHERE name= :setlistName")
    void deleteByName(String setlistName);
    @Update
    void update(Setlist updated);

}
