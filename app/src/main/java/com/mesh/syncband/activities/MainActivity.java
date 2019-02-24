package com.mesh.syncband.activities;


import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment home = manager.findFragmentByTag(HOME_FRAGMENT);
        if(home != null){
            finish();
            return;
        }
        super.onBackPressed();
        updateIconNavigationMenu(manager);
    }

    private void updateIconNavigationMenu(FragmentManager fragmentManager){

        final Fragment server = fragmentManager.findFragmentByTag(SERVER_FRAGMENT);
        if(server != null){
            if(server.isVisible())
                navigationView.setCheckedItem(R.id.nav_home);
        }

        final Fragment setlists = fragmentManager.findFragmentByTag(SETLISTS_FRAGMENT);
        if(setlists != null){
            if(setlists.isVisible())
                navigationView.setCheckedItem(R.id.nav_setlists);
        }

        final Fragment perfil = fragmentManager.findFragmentByTag(PERFIL_FRAGMENT);
        if(perfil != null){
            if(perfil.isVisible())
                navigationView.setCheckedItem(R.id.nav_perfil);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        // Handle navigation view item clicks here.
        if (id == R.id.nav_home) {
            replaceFragment(new HomeFragment(), HOME_FRAGMENT);
        } else if (id == R.id.nav_server) {
            replaceFragment(new ServerFragment(), SERVER_FRAGMENT);
        } else if (id == R.id.nav_setlists) {
            replaceFragment(new SetlistsFragment(), SETLISTS_FRAGMENT);
        } else if (id == R.id.nav_perfil) {
            replaceFragment(new PerfilFragment(), PERFIL_FRAGMENT);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
