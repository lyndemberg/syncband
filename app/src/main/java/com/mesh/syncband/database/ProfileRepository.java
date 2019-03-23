package com.mesh.syncband.database;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.mesh.syncband.model.Profile;

public class ProfileRepository {

    private final ProfileDao profileDao;

    public ProfileRepository(Context ctx) {
        profileDao = AppDatabase.getAppDatabase(ctx).getProfileDao();
    }

    public void insertProfile(final Profile profile){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                profileDao.save(profile);
                return null;
            }
        }.execute();
    }

    public void updateProfile(final Profile updated){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                profileDao.update(updated);
                return null;
            }
        }.execute();
    }

    public void deleteProfile(final Profile delete){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                profileDao.delete(delete);
                return null;
            }
        }.execute();
    }

    public LiveData<Profile> getProfile(){
        return profileDao.getProfile();
    }

    public Profile getProfileSync(){
        return profileDao.getProfileSync();
    }

}
