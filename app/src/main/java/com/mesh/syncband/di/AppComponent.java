package com.mesh.syncband.di;

import com.mesh.syncband.activities.MainActivity;
import com.mesh.syncband.activities.ManagerSetlistActivity;
import com.mesh.syncband.fragments.HomeFragment;
import com.mesh.syncband.fragments.PerfilFragment;
import com.mesh.syncband.fragments.ServerFragment;
import com.mesh.syncband.fragments.SetlistsFragment;
import com.mesh.syncband.fragments.dialog.AuthenticationDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity homeFragment);
    void inject(HomeFragment homeFragment);
    void inject(PerfilFragment perfilFragment);
    void inject(ManagerSetlistActivity managerSetlistActivity);
    void inject(ServerFragment serverFragment);
    void inject(SetlistsFragment setlistsFragment);
    void inject(AuthenticationDialog authenticationDialog);

}
