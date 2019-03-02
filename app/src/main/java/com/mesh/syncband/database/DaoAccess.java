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
public interface DaoAccess {

    //access @Entity Profile
    @Query("SELECT * FROM profile")
    LiveData<Profile> getProfile();
    @Insert
    void save(Profile profile);
    @Delete
    void delete(Profile profile);
    @Update
    void update(Profile updated);

    //access @Entity Setlist
    @Query("SELECT * FROM setlist WHERE id=:id")
    LiveData<Setlist> findById(int id);
    @Query("SELECT * FROM setlist WHERE name=:name")
    LiveData<Setlist> findByName(String name);
    @Query("SELECT * FROM setlist")
    LiveData<List<Setlist>> getAll();
    @Query("SELECT name FROM setlist")
    LiveData<List<String>> getAllNames();
    @Insert
    void save(Setlist setlist);
    @Delete
    void delete(Setlist setlist);
    @Update
    void update(Setlist updated);

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
