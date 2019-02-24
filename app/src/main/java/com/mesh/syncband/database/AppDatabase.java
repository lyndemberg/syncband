package com.mesh.syncband.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.mesh.syncband.database.converters.RoomTypeConverters;
import com.mesh.syncband.database.dao.ProfileDao;
import com.mesh.syncband.database.dao.SetlistDao;
import com.mesh.syncband.database.dao.SongDao;
import com.mesh.syncband.data.Profile;
import com.mesh.syncband.data.Setlist;
import com.mesh.syncband.data.Song;

@Database(entities = {
        Profile.class,
        Setlist.class,
        Song.class
}, version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract ProfileDao profileDao();
    public abstract SetlistDao setlistDao();
    public abstract SongDao songDao();

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
