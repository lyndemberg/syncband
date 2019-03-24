package com.mesh.syncband.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.ProfileDao;
import com.mesh.syncband.database.SetlistDao;
import com.mesh.syncband.database.SongDao;
import com.mesh.syncband.grpc.MetronomeServer;
import com.mesh.syncband.grpc.MetronomeServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase(Context context){
        return Room.databaseBuilder(context,
                    AppDatabase.class,"database-syncband")
                    .fallbackToDestructiveMigration()
                    .build();
    }

    @Singleton
    @Provides
    public ProfileDao provideProfileDao(AppDatabase appDatabase){
        return appDatabase.getProfileDao();
    }

    @Singleton
    @Provides
    public SetlistDao provideSetlistDao(AppDatabase appDatabase){
        return appDatabase.getSetlistDao();
    }

    @Singleton
    @Provides
    public SongDao provideSongDao(AppDatabase appDatabase){
        return appDatabase.getSongDao();
    }

    @Singleton
    @Provides
    public MetronomeServer provideMetronomeServer(){
        return new MetronomeServer();
    }

}
