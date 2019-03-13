package com.mesh.syncband.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mesh.syncband.R;
import com.mesh.syncband.model.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {


    public static class SongHolder extends RecyclerView.ViewHolder{
        final AppCompatCheckBox checkBox;
        final TextView labelSong;
        final TextView labelArtist;
        final TextView labelBpmValue;

        public SongHolder(View itemView) {
            super(itemView);
            labelSong = itemView.findViewById(R.id.label_song_name);
            labelArtist = itemView.findViewById(R.id.label_artist_name);
            labelBpmValue = itemView.findViewById(R.id.label_bpm_value);
            checkBox = itemView.findViewById(R.id.check_song);
        }
    }

    private Context context;
    private List<Song> songs;
    private List<Integer> checkedItems;
    private View.OnClickListener onClickListener;
    private boolean showCheckBoxes;


    public SongAdapter(Context context, List<Song> data) {
        this.songs = data;
        this.context = context;
        this.showCheckBoxes = false;
        this.checkedItems = new ArrayList<>();
    }

    public void setOnClickListener(View.OnClickListener clickListener){
        this.onClickListener = clickListener;
    }

    public void setShowCheckBox(boolean show){
        this.showCheckBoxes = show;
    }

    public void clearCheckedItems(){
        this.checkedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(context)
                .inflate(R.layout.song_adapter_layout, parent, false);
        viewRow.setOnClickListener(onClickListener);
        SongHolder holder = new SongHolder(viewRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SongHolder holder, int position) {
        Song song = songs.get(position);

        if(showCheckBoxes){
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.checkBox.setVisibility(View.GONE);
            checkedItems = new ArrayList<>();
        }

        if(checkedItems.contains(position)){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                checkedItems.add(holder.getAdapterPosition());
            }
        });

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

    public boolean isShowCheckBoxes() {
        return showCheckBoxes;
    }

    public List<Integer> getPositionsChecked(){
        return checkedItems;
    }
}
