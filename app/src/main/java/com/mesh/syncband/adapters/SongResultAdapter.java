package com.mesh.syncband.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.valueobject.SongResult;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongResultAdapter extends RecyclerView.Adapter<SongResultAdapter.SongResultHolder> {

    public static class SongResultHolder extends RecyclerView.ViewHolder{

        final ImageView photoAlbum;
        final TextView labelSong;
        final TextView labelArtist;
        final TextView labelBpmValue;

        public SongResultHolder(View itemView) {
            super(itemView);
            photoAlbum = itemView.findViewById(R.id.photo_result);
            labelSong = itemView.findViewById(R.id.label_song_result);
            labelArtist = itemView.findViewById(R.id.label_artist_result);
            labelBpmValue = itemView.findViewById(R.id.label_bpm_result);
        }
    }

    private List<SongResult> results;
    private Context context;
    private final View.OnClickListener listener;

    public SongResultAdapter(Context context, List<SongResult> songs, View.OnClickListener listener){
        this.results = songs;
        this.context = context;
        this.listener = listener;
    }

    public void setDataset(List<SongResult> songs){
        this.results = songs;
    }
    @NonNull
    @Override
    public SongResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(context)
                .inflate(R.layout.song_result_item, parent, false);
        viewRow.setOnClickListener(listener);
        SongResultHolder holder = new SongResultHolder(viewRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SongResultHolder holder, int position) {
        SongResult songResult = results.get(position);
        Picasso.get()
                .load(songResult.getPhoto())
                .error(R.drawable.circle_song)
                .into(holder.photoAlbum);
        holder.labelSong.setText(songResult.getName());
        holder.labelArtist.setText(songResult.getArtist());
        holder.labelBpmValue.setText(Math.round(songResult.getBpm())+"");
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public SongResult getItem(int position){
        return results.get(position);
    }

}
