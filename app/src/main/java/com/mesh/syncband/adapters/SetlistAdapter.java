package com.mesh.syncband.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mesh.syncband.R;

import java.util.ArrayList;
import java.util.List;

public class SetlistAdapter extends RecyclerView.Adapter<SetlistAdapter.SetlistHolder> {

    private Context context;
    private List<String> setlists;
    private List<Integer> checkedItems;
    private View.OnClickListener onClickListener;
    private boolean showCheckBoxes;

    public SetlistAdapter(Context context, List<String> data, View.OnClickListener onClickListener){
        this.context = context;
        this.setlists = data;
        this.onClickListener = onClickListener;
        this.showCheckBoxes = false;
        this.checkedItems = new ArrayList<>();
    }

    public void clearCheckedItems(){
        this.checkedItems = new ArrayList<>();
    }

    public void setShowCheckBox(boolean show){
        this.showCheckBoxes = show;
    }

    @NonNull
    @Override
    public SetlistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(context)
                .inflate(R.layout.setlist_item, parent, false);
        viewRow.setOnClickListener(onClickListener);
        SetlistHolder setlistHolder = new SetlistHolder(viewRow);
        return setlistHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SetlistHolder holder, int position) {
        String item = setlists.get(position);

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

        holder.labelSetlist.setText(item);
    }

    @Override
    public int getItemCount() {
        return setlists.size();
    }

    public boolean isShowCheckBoxes() {
        return showCheckBoxes;
    }

    public void setDataset(List<String> data){
        this.setlists = data;
    }
    public List<Integer> getPositionsChecked(){
        return checkedItems;
    }
    public String getItem(int position) {
        return setlists.get(position);
    }


    public static class SetlistHolder extends RecyclerView.ViewHolder{
        final AppCompatCheckBox checkBox;
        final TextView labelSetlist;
        
        public SetlistHolder(View itemView) {
            super(itemView);
            labelSetlist = itemView.findViewById(R.id.label_setlist_name);
            checkBox = itemView.findViewById(R.id.check_setlist);
        }
    }


}
