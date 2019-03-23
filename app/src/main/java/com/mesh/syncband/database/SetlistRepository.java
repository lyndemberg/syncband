package com.mesh.syncband.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;

import java.util.List;

public class SetlistRepository {

    private final SetlistDao setlistDao;

    public SetlistRepository(Context ctx) {
        setlistDao = AppDatabase.getAppDatabase(ctx).getSetlistDao();
    }

    public void insertSetlist(final Setlist setlist){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                setlistDao.save(setlist);
                return null;
            }
        }.execute();
    }

    public void updateSetlist(final Setlist updated){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                setlistDao.update(updated);
                return null;
            }
        }.execute();
    }

    public void deleteSetlist(final Setlist delete){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                setlistDao.delete(delete);
                return null;
            }
        }.execute();
    }

    public void deleteSetlistByName(final String setlistName){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                setlistDao.deleteByName(setlistName);
                return null;
            }
        }.execute();
    }

    public LiveData<List<Setlist>> getAll(){
        return setlistDao.getAll();
    }

    public LiveData<List<String>> getAllNames(){
        return setlistDao.getAllNames();
    }

    public LiveData<Setlist> findById(int id){
        return setlistDao.findById(id);
    }

    public LiveData<Setlist> findByName(String name){
        return setlistDao.findByName(name);
    }

    public Setlist findByNameSync(String name){
        return setlistDao.findByNameSync(name);
    }

}
