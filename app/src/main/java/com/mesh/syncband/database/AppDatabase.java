package com.mesh.syncband.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.mesh.syncband.database.converters.RoomTypeConverters;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;

@Database(entities = {
        Profile.class,
        Setlist.class,
        Song.class
}, version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DaoAccess daoAccess();

    public static AppDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class,"database-syncband")
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return INSTANCE;
    }

}
