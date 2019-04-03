package com.mesh.syncband.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mesh.syncband.R;

import com.mesh.syncband.fragments.HomeFragment;
import com.mesh.syncband.fragments.PerfilFragment;
import com.mesh.syncband.fragments.ServerFragment;
import com.mesh.syncband.fragments.SetlistsFragment;
import com.mesh.syncband.interfaces.ActivityBindMetronome;
import com.mesh.syncband.services.IMetronome;
import com.mesh.syncband.services.MetronomeService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ActivityBindMetronome {

    private static final String TAG = "activities.MainActivity";

    DrawerLayout drawer;
    NavigationView navigationView;

    private Intent intentMetronome;
    private IMetronome iMetronome;

    //connection with services.MetronomeService
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MetronomeService.MetronomeServiceBinder connection = (MetronomeService.MetronomeServiceBinder) iBinder;
            iMetronome = connection.getInterface();
            Log.d(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iMetronome = null;
            Log.d(TAG,"onServiceDisconnected()");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final FragmentManager manager = getSupportFragmentManager();
                manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        Log.d("BEGA","asdfdf");
                        Log.d("BEGA", String.valueOf(manager.getBackStackEntryCount()));
                    }
                });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment(),HomeFragment.class.getSimpleName())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        intentMetronome = new Intent(this,MetronomeService.class);
//        startService(intentMetronome);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            Fragment home = getSupportFragmentManager().findFragmentByTag(HomeFragment.class.getSimpleName());
            if(home!=null && home.isVisible()){
                finish();
                return;
            }
            super.onBackPressed();
            updateIconSelectedNavigationMenu();
        }
    }

    private void updateIconSelectedNavigationMenu(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment instanceof HomeFragment){
            navigationView.setCheckedItem(R.id.nav_home);
        }else if(fragment instanceof PerfilFragment){
            navigationView.setCheckedItem(R.id.nav_perfil);
        }else if(fragment instanceof ServerFragment){
            navigationView.setCheckedItem(R.id.nav_server);
        }else if(fragment instanceof SetlistsFragment){
            navigationView.setCheckedItem(R.id.nav_setlists);
        }
    }

    private void replaceFragment(Fragment fragment, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();

    }

    private void replaceWithoutAddToStack(Fragment fragment, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass;
        switch (id){
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_server:
                fragmentClass = ServerFragment.class;
                break;
            case R.id.nav_setlists:
                fragmentClass = SetlistsFragment.class;
                break;
            case R.id.nav_perfil:
                fragmentClass = PerfilFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(fragmentClass.getSimpleName());
        if(fragmentByTag!=null){
            replaceFragment(fragmentByTag,fragmentClass.getSimpleName());
        }else{
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            replaceFragment(fragment,fragmentClass.getSimpleName());
        }

        drawer.closeDrawers();
        setTitle(item.getTitle());
        item.setChecked(true);


        return true;
    }


    @Override
    protected void onStop() {
        if(iMetronome != null){
            Log.d(TAG,"somente unbind");
            unbindService(connection);
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        startService(intentMetronome);
        boolean connected = bindService(intentMetronome, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG,"Metronome Service connected: "+connected);
        super.onStart();
    }

    @Override
    public IMetronome getMetronomeService() {
        return iMetronome;
    }
}
