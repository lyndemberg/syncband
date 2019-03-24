package com.mesh.syncband;

import android.support.multidex.MultiDexApplication;

import com.mesh.syncband.di.AppComponent;
import com.mesh.syncband.di.AppModule;
import com.mesh.syncband.di.DaggerAppComponent;

public class MainApplication extends MultiDexApplication {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        initDagger();
    }

    private void initDagger() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }
}
