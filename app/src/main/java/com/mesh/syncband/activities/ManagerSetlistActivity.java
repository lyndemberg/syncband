package com.mesh.syncband.activities;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.mesh.syncband.R;
import com.mesh.syncband.adapters.SongAdapter;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.database.SongRepository;
import com.mesh.syncband.fragments.dialog.SongAddOptionsDialog;
import com.mesh.syncband.fragments.dialog.SongDialog;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;
import com.mesh.syncband.valueobject.SongResult;

import java.util.ArrayList;
import java.util.List;

public class ManagerSetlistActivity extends AppCompatActivity
        implements SongAddOptionsDialog.SongOptionsListener, SongDialog.NewSongListener {

    String currentSetlist = "";
    private SetlistRepository setlistRepository;
    private SongRepository songRepository;
    private SongAdapter songAdapter;
    private ListView listViewSongs;
    private Setlist setlist = null;
    private List<Song> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_setlist);

        Toolbar toolbar = findViewById(R.id.toolbar_manager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setlistRepository = new SetlistRepository(this);
        songRepository = new SongRepository(this);

        songAdapter = new SongAdapter(this, songsList);

        listViewSongs = findViewById(R.id.list_songs);
        listViewSongs.setAdapter(songAdapter);

        //load setlist
        currentSetlist = getIntent().getStringExtra("currentSetlist");
        setlistRepository.findByName(currentSetlist).observe(this,new Observer<Setlist>(){
            @Override
            public void onChanged(@Nullable Setlist set) {
                setlist = set;
                refreshSongs();
            }
        });
        //

        final EditText inputNameSetlist = findViewById(R.id.input_name_setlist);
        inputNameSetlist.setText(currentSetlist);

        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Song item = songAdapter.getItem(i);
                SongDialog songDialog = new SongDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable("song",item);
                songDialog.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                songDialog.show(transaction, SongDialog.class.getSimpleName());
            }
        });


        FloatingActionButton floatButton = findViewById(R.id.button_add_song);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SongAddOptionsDialog songOptions = new SongAddOptionsDialog();
            songOptions.show(transaction, SongAddOptionsDialog.class.getSimpleName());
            }
        });
    }

    private void refreshSongs(){
        songRepository.findAllBySetlist(setlist.getId()).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                songAdapter.clear();
                songAdapter.addAll(songs);
                songsList = songs;
                if (songs.isEmpty())
                    findViewById(R.id.songs_message).setVisibility(View.VISIBLE);
                else
                    findViewById(R.id.songs_message).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void notifySongSelected(SongAddOptionsDialog.SongOption option) {
        if(option.equals(SongAddOptionsDialog.SongOption.MANUAL)){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SongDialog songDialog = new SongDialog();
            songDialog.show(transaction, SongDialog.class.getSimpleName());
        }else if(option.equals(SongAddOptionsDialog.SongOption.SPOTIFY)){
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent,SearchActivity.SEARCH_REQUEST);
        }
    }

    @Override
    public void toSave(Song song) {
        song.setIdSetlist(setlist.getId());
        songRepository.insertSong(song);
    }

    @Override
    public void toUpdate(Song song) {
        songRepository.updateSong(song);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SearchActivity.SEARCH_REQUEST && resultCode == SearchActivity.SONG_SELECTED){
            SongResult songResult = (SongResult) data.getSerializableExtra(SearchActivity.SONG_SELECTED_TO_ADD);
            Song toSave = new Song();
            toSave.setName(songResult.getName());
            toSave.setArtist(songResult.getArtist());
            Long bpm = Math.round(songResult.getBpm());
            toSave.setBpm(bpm.intValue());
            toSave(toSave);
        }
    }
}
