package com.mesh.syncband.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.mesh.syncband.R;

public class SongAddOptionsDialog extends DialogFragment {

    public enum SongOption{
        MANUAL,
        SPOTIFY
    }

    public interface SongOptionsListener{
        void notifySongSelected(SongOption option);
    }

//    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_song_options, container, false);

        ImageButton spotifyOption = view.findViewById(R.id.button_spotify_option);
        ImageButton manualOption = view.findViewById(R.id.button_manual_option);
        spotifyOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongOptionsListener listenerOptions = (SongOptionsListener) getActivity();
                listenerOptions.notifySongSelected(SongOption.SPOTIFY);
                dismiss();
            }
        });

        manualOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongOptionsListener listenerOptions = (SongOptionsListener) getActivity();
                listenerOptions.notifySongSelected(SongOption.MANUAL);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
    }




}
