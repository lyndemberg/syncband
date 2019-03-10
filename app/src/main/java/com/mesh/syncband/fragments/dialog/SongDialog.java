package com.mesh.syncband.fragments.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.model.Song;

public class SongDialog extends DialogFragment {

    private Song song = null;
    private SongDialogListener listener;

    public interface SongDialogListener{
        void toSave(Song song);
        void toUpdate(Song song);
    }

    public SongDialog(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            song  = (Song) getArguments().get("song");
        }
        listener = (SongDialogListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.song_dialog_fragment, container, false);

        final TextView title = view.findViewById(R.id.song_dialog_title);
        final NumberPicker pickerBpm = view.findViewById(R.id.picker_bpm);
        final TextInputEditText inputNameSong = view.findViewById(R.id.input_name_song);
        final TextInputEditText inputNameArtist = view.findViewById(R.id.input_name_artist);

        int minValue = getResources().getInteger(R.integer.bpm_min);
        int maxValue = getResources().getInteger(R.integer.bpm_max);
        pickerBpm.setMinValue(minValue);
        pickerBpm.setMaxValue(maxValue);

        if (song != null) {
            title.setText("Editar musica");
            inputNameSong.setText(song.getName());
            inputNameArtist.setText(song.getArtist());
            pickerBpm.setValue(song.getBpm());
        }else{
            title.setText("Nova musica");
            int defaultValue = getResources().getInteger(R.integer.default_bpm);
            pickerBpm.setValue(defaultValue);
        }

        pickerBpm.setWrapSelectorWheel(true);


        FloatingActionButton buttonAddSong = view.findViewById(R.id.button_add_song);
        buttonAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(song != null){
                    song.setName(inputNameSong.getText().toString());
                    song.setArtist(inputNameArtist.getText().toString());
                    song.setBpm(pickerBpm.getValue());
                    listener.toUpdate(song);
                }else{
                    Song toSave = new Song();
                    toSave.setName(inputNameSong.getText().toString());
                    toSave.setArtist(inputNameArtist.getText().toString());
                    toSave.setBpm(pickerBpm.getValue());
                    listener.toSave(toSave);
                }

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
