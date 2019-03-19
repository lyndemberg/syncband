package com.mesh.syncband.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mesh.syncband.R;

public class HomeFragment extends Fragment {

    private TextView status;
    private TextView currentBpm;
    private TextView currentSong;
    private Button buttonDisconnect;
    private LinearLayout layoutVolume;
    private Button buttonSearch;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonSearch = view.findViewById(R.id.button_search);

        status = view.findViewById(R.id.status);
        currentBpm = view.findViewById(R.id.current_bpm);
        currentSong = view.findViewById(R.id.current_song);
        buttonDisconnect = view.findViewById(R.id.button_disconnect);
        layoutVolume = view.findViewById(R.id.layout_volume);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("SyncBand - Home");
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
