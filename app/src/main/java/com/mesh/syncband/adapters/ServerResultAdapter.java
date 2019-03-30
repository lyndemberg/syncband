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
import com.mesh.syncband.grpc.DeviceData;

import java.util.ArrayList;
import java.util.List;

public class ServerResultAdapter extends RecyclerView.Adapter<ServerResultAdapter.ServerResultHolder> {

    private Context context;
    private List<DeviceData> servers;
    private View.OnClickListener onClickListener;

    public ServerResultAdapter(Context context,View.OnClickListener onClickListener){
        this.context = context;
        this.onClickListener = onClickListener;
        this.servers = new ArrayList<>();
    }

    public void addItem(DeviceData deviceData){
        servers.add(deviceData);
    }

    @NonNull
    @Override
    public ServerResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(context)
                .inflate(R.layout.server_result_item, parent, false);
        viewRow.setOnClickListener(onClickListener);
        ServerResultHolder holder = new ServerResultHolder(viewRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServerResultHolder holder, int position) {
        DeviceData deviceData = servers.get(position);
        holder.textNickname.setText(deviceData.getNickname());
        holder.textFunction.setText(deviceData.getFunction());
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    public DeviceData getItem(int position){
        return servers.get(position);
    }

    public static class ServerResultHolder extends RecyclerView.ViewHolder{

        final ImageView icon;
        final TextView textNickname;
        final TextView textFunction;

        public ServerResultHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_server);
            textNickname = itemView.findViewById(R.id.text_nickname);
            textFunction = itemView.findViewById(R.id.text_function);
        }
    }

}
