package com.mesh.syncband.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mesh.syncband.data.Profile;

@Dao
public interface ProfileDao {

    @Query("SELECT * FROM profile")
    Profile getProfile();
    @Insert
    void save(Profile profile);
    @Delete
    void delete(Profile profile);
    @Update
    void update(Profile updated);
}
