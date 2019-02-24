package com.mesh.syncband.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mesh.syncband.data.Setlist;

import java.util.List;

@Dao
public interface SetlistDao {

    @Query("SELECT * FROM setlist WHERE id=:id")
    Setlist findById(int id);
    @Query("SELECT * FROM setlist WHERE name=:name")
    Setlist findByName(String name);
    @Query("SELECT * FROM setlist")
    List<Setlist> getAll();
    @Insert
    void save(Setlist setlist);
    @Delete
    void delete(Setlist setlist);
    @Update
    void update(Setlist updated);

}
