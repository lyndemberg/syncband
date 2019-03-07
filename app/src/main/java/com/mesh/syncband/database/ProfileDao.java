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
public interface ProfileDao {

    //access @Entity Profile
    @Query("SELECT * FROM profile")
    LiveData<Profile> getProfile();
    @Insert
    void save(Profile profile);
    @Delete
    void delete(Profile profile);
    @Update
    void update(Profile updated);
}
