package com.mesh.syncband.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Song;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SongRepository {

    private final SongDao songDao;

    @Inject
    public SongRepository(SongDao songDao) {
        this.songDao = songDao;
    }

    public void insertSong(final Song song){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                songDao.save(song);
                return null;
            }
        }.execute();
    }

    public void updateSong(final Song updated){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                songDao.update(updated);
                return null;
            }
        }.execute();
    }

    public void deleteSong(final Song delete){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                songDao.delete(delete);
                return null;
            }
        }.execute();
    }

    public LiveData<List<Song>> findAllBySetlist(int idSetlist){
        return songDao.findAllBySetlist(idSetlist);
    }
    public List<Song> findAllBySetlistSync(int idSetlist){
        return songDao.findAllBySetlistSync(idSetlist);
    }

}
