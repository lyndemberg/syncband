package com.mesh.syncband.fragments;


import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.adapters.SongAdapter;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.DaoAccess;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenSetlist extends Fragment {

    String currentSetlist = "";
    private DaoAccess daoAccess;
    private SongAdapter songAdapter;
    private ListView listViewSongs;
    private Setlist setlist = null;

    public OpenSetlist() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoAccess = AppDatabase.getAppDatabase(getContext()).daoAccess();
        songAdapter = new SongAdapter(getContext());
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
        daoAccess.findByName(currentSetlist).observe(this,new Observer<Setlist>(){
            @Override
            public void onChanged(@Nullable Setlist set) {
                setlist = set;
                //load setlist songs
                daoAccess.findAllBySetlist(setlist.getId()).observe(OpenSetlist.this, new Observer<List<Song>>() {
                    @Override
                    public void onChanged(@Nullable List<Song> songs) {
                        songAdapter.clear();
                        songAdapter.addAll(songs);
                    }
                });
                //

            }
        });
        //

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getActivity().setTitle("Editar setlist");
    }



}
