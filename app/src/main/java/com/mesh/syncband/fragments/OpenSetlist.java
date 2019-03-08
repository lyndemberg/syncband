package com.mesh.syncband.fragments;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.mesh.syncband.R;
import com.mesh.syncband.adapters.SongAdapter;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.database.SongRepository;
import com.mesh.syncband.fragments.dialog.SongDialog;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenSetlist extends Fragment {

    String currentSetlist = "";
    private SetlistRepository setlistRepository;
    private SongRepository songRepository;
    private SongAdapter songAdapter;
    private ListView listViewSongs;
    private Setlist setlist = null;

    public OpenSetlist() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlistRepository = new SetlistRepository(getContext());
        songRepository = new SongRepository(getContext());
//        songAdapter = new SongAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_open_setlist, container, false);

        currentSetlist = getArguments().getString("currentSetlist");
        EditText inputNameSetlist = view.findViewById(R.id.input_name_setlist);
        inputNameSetlist.setText(currentSetlist);

        listViewSongs = view.findViewById(R.id.list_songs);
        listViewSongs.setAdapter(songAdapter);
        //load setlist
        setlistRepository.findByName(currentSetlist).observe(this,new Observer<Setlist>(){
            @Override
            public void onChanged(@Nullable Setlist set) {
            setlist = set;
            //load setlist songs
                refreshSongs();
            //
            }
        });
        //

        FloatingActionButton floatButton = view.findViewById(R.id.button_add_song);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                SongDialog songDialog = new SongDialog();
                songDialog.show(transaction,"dialog_fragment");
            }
        });
        return view;
    }

    private void refreshSongs(){
        songRepository.findAllBySetlist(setlist.getId()).observe(OpenSetlist.this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable List<Song> songs) {
                songAdapter.clear();
                songAdapter.addAll(songs);
                if (songs.isEmpty())
                    getView().findViewById(R.id.songs_message).setVisibility(View.VISIBLE);
                else
                    getView().findViewById(R.id.songs_message).setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getActivity().setTitle("Editar setlist");
    }



}
