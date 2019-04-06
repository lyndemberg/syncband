package com.mesh.syncband.fragments.dialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.mesh.syncband.R;
import com.mesh.syncband.adapters.ServerResultAdapter;
import com.mesh.syncband.grpc.DeviceData;
import com.mesh.syncband.grpc.MetronomeServiceGrpc;
import com.mesh.syncband.grpc.Void;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;
import java.util.ArrayList;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ListServersDialog extends DialogFragment {

    private static final String TAG = "ListServersDialog";

    private RecyclerView recyclerView;
    private ServerResultAdapter adapter;
    private ProgressBar progressBar;

    public interface ListServersListener{
        void notifyServerSelected(DeviceData deviceData);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.servers_dialog_fragment, container, false);
        recyclerView = view.findViewById(R.id.list_servers_result);
        getDialog().setTitle("Buscando servidores");
        adapter = new ServerResultAdapter(getContext(), new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int positionAdapter = recyclerView.getChildAdapterPosition(view);
                DeviceData item = adapter.getItem(positionAdapter);
                ListServersListener listener = (ListServersListener) getParentFragment();
                listener.notifyServerSelected(item);
                dismiss();
            }

        });

        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
        progressBar = view.findViewById(R.id.progress_search_servers);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        progressBar.setVisibility(View.VISIBLE);
        SearchServersTask searchServersTask = new SearchServersTask();
        searchServersTask.execute();
    }

    private class SearchServersTask extends AsyncTask<Void,DeviceData,Void> {

        private static final int PORT = 44444;

        @Override
        protected Void doInBackground(Void... voids) {

            SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                @Override
                public void onDeviceFound(final Device device) {
                    ManagedChannel channel = ManagedChannelBuilder.forAddress(device.ip, PORT).usePlaintext().build();
                    MetronomeServiceGrpc.MetronomeServiceStub stub = MetronomeServiceGrpc.newStub(channel);
                    stub.ping(Void.newBuilder().build(), new StreamObserver<DeviceData>() {
                        private DeviceData deviceData;
                        @Override
                        public void onNext(DeviceData value) {
                            this.deviceData = value;
                            publishProgress(value);
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.d(TAG,"Not server"+ device.ip);
                        }

                        @Override
                        public void onCompleted() {

                        }
                    });
                }
                @Override
                public void onFinished(ArrayList<Device> devicesFound) {
                    publishProgress(null);
                }
            });


            return null;
        }

        @Override
        protected void onProgressUpdate(DeviceData... values) {
            if(values==null){
                progressBar.setVisibility(View.INVISIBLE);
            }else{
                DeviceData deviceData = values[0];
                adapter.addItem(deviceData);
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
