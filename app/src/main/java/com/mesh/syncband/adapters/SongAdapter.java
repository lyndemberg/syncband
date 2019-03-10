package com.mesh.syncband.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.model.Song;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {


    public static class SongHolder extends RecyclerView.ViewHolder{
        final TextView labelSong;
        final TextView labelArtist;
        final TextView labelBpmValue;

        public SongHolder(View itemView) {
            super(itemView);
            labelSong = itemView.findViewById(R.id.label_song_name);
            labelArtist = itemView.findViewById(R.id.label_artist_name);
            labelBpmValue = itemView.findViewById(R.id.label_bpm_value);
        }
    }

    private Context context;
    private List<Song> songs;
    private final View.OnClickListener listener;

    public SongAdapter(Context context, List<Song> data, View.OnClickListener listener) {
        this.songs = data;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(context)
                .inflate(R.layout.song_adapter_layout, parent, false);
        viewRow.setOnClickListener(listener);
        SongHolder holder = new SongHolder(viewRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongHolder holder, int position) {
        Song song = songs.get(position);
        holder.labelSong.setText(song.getName());
        holder.labelArtist.setText(song.getArtist());
        holder.labelBpmValue.setText(song.getBpm()+"");
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setDataset(List<Song> data){
        this.songs = data;
    }


    public Song getItem(int position) {
        return songs.get(position);
    }

}
