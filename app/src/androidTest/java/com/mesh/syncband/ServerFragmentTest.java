package com.mesh.syncband;

import android.arch.persistence.room.Room;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.mesh.syncband.activities.MainActivity;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.AppDatabase_Impl;
import com.mesh.syncband.database.ProfileDao;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.database.SetlistDao;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.fragments.ServerFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class ServerFragmentTest{

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private List<String> setlists;


    @Before
    public void startServerFragment(){
        loadStringsSetlists();

        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ServerFragment(), ServerFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Test
    public void testIsPossibleStart(){
        if(setlists.isEmpty()){
            //assert that once message visible
            onView(withId(R.id.spinner_setlists)).check(matches(not(isDisplayed())));
            onView(withId(R.id.buttonIniciar)).check(matches(not(isDisplayed())));
            onView(withId(R.id.layout_input_password)).check(matches(not(isDisplayed())));
            // assert visible
            onView(withId(R.id.msg_not_setlists)).check(matches(isDisplayed()));
        }else{
            //assert that once message visible
            onView(withId(R.id.spinner_setlists)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonIniciar)).check(matches(isDisplayed()));
            onView(withId(R.id.layout_input_password)).check(matches(isDisplayed()));
            // assert visible
            onView(withId(R.id.msg_not_setlists)).check(matches(not(isDisplayed())));
        }
    }


    private void loadStringsSetlists(){
        this.setlists =  Room.databaseBuilder(activityActivityTestRule.getActivity(),
                AppDatabase.class,"database-syncband")
                .fallbackToDestructiveMigration()
                .build()
                .getSetlistDao().getAllNamesSync();
    }
}