package com.mesh.syncband.activities;


import android.os.Bundle;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String HOME_FRAGMENT = "HOME_FRAGMENT";
    private static final String PERFIL_FRAGMENT = "PERFIL_FRAGMENT";
    private static final String SERVER_FRAGMENT = "SERVER_FRAGMENT";
    private static final String SETLISTS_FRAGMENT = "SETLISTS_FRAGMENT";

    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView.OnNavigationItemSelectedListener navigationListener = new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                int id = item.getItemId();
                // Handle navigation view item clicks here.
                if (id == R.id.nav_home) {
                    replaceFragment(new HomeFragment(), HOME_FRAGMENT);
                    return true;
                } else if (id == R.id.nav_server) {
                    replaceFragment(new ServerFragment(), SERVER_FRAGMENT);
                    return true;
                } else if (id == R.id.nav_setlists) {
                    replaceFragment(new SetlistsFragment(), SETLISTS_FRAGMENT);
                    return true;
                } else if (id == R.id.nav_perfil) {
                    replaceFragment(new PerfilFragment(), PERFIL_FRAGMENT);
                    return true;
                }
                return false;
            }
        };

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navigationListener);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            FragmentManager manager = getSupportFragmentManager();
            Fragment home = manager.findFragmentByTag(HOME_FRAGMENT);
            if(home != null){
                finish();
                return;
            }
            super.onBackPressed();
            updateIconSelectedNavigationMenu(manager);
        }
    }

    private void updateIconSelectedNavigationMenu(FragmentManager fragmentManager){
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
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
        transaction.commit();
    }
}
