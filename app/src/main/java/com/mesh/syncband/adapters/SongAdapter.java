package com.mesh.syncband.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.model.Song;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    private Context ctx;
    private List<Song> songs;
    private List<AppCompatCheckBox> checkBoxList;

    public SongAdapter(@NonNull Context context, @NonNull List<Song> data) {
        super(context, 0, data);
        this.songs = data;
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_adapter_layout,parent,false);

        }

        TextView labelSong = convertView.findViewById(R.id.label_song_name);
        labelSong.setText(song.getName());
        TextView labelArtist = convertView.findViewById(R.id.label_artist_name);
        labelArtist.setText(song.getArtist());
        TextView labelBpmValue = convertView.findViewById(R.id.label_bpm_value);
        labelBpmValue.setText(""+song.getBpm());

        return convertView;
    }


    @Nullable
    @Override
    public Song getItem(int position) {
        return songs.get(position);
    }
}
