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
import com.mesh.syncband.valueobject.SongVo;

import java.util.List;

public class SongVoAdapter extends ArrayAdapter<SongVo> {

    private Context ctx;
    private List<SongVo> songs;

    public SongVoAdapter(@NonNull Context context, @NonNull List<SongVo> data) {
        super(context, 0, data);
        this.songs = data;
        this.ctx = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SongVo song = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_result_item,parent,false);

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
    public SongVo getItem(int position) {
        return songs.get(position);
    }
}
