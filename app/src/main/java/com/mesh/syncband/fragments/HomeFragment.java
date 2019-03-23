package com.mesh.syncband.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.socket.valueobject.DeviceData;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView status;
    private TextView currentBpm;
    private TextView currentSong;
    private Button buttonDisconnect;
    private LinearLayout layoutVolume;
    private Button buttonSearch;
    private List<DeviceData> serversFound;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serversFound = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonSearch = view.findViewById(R.id.button_search);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchServersTask().execute();
            }
        });

        status = view.findViewById(R.id.status);
        currentBpm = view.findViewById(R.id.current_bpm);
        currentSong = view.findViewById(R.id.current_song);
        buttonDisconnect = view.findViewById(R.id.button_disconnect);
        layoutVolume = view.findViewById(R.id.layout_volume);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("SyncBand - Home");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void showItensDisconnect(){

    }

    private void showItensConnect(){

    }

    private class SearchServersTask extends AsyncTask<Void,Void,List<DeviceData>>{
        private static final int PORT = 44444;
        private ExecutorService executorService;
        private volatile int count = 0;
        @Override
        protected List<DeviceData> doInBackground(Void... voids) {

            final List<DeviceData> deviceDataList = new ArrayList<>();
            SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
                @Override
                public void onDeviceFound(final Device device) {
                }
                @Override
                public void onFinished(ArrayList<Device> devicesFound) {
//                    executorService = Executors.newFixedThreadPool(2);
                    for(final Device device : devicesFound){
                        Runnable ping = new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    Socket socket = new Socket(device.ip, PORT);
                                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                                    JSONObject request = new JSONObject();
                                    request.put("action","PING");
                                    out.writeObject(request);
                                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                                    DeviceData deviceData = (DeviceData) in.readObject();
                                    deviceDataList.add(deviceData);
                                    socket.close();
                                    count++;
                                } catch (IOException e) {
                                    count++;
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    count++;
                                    e.printStackTrace();
                                } catch (ClassNotFoundException e) {
                                    count++;
                                    e.printStackTrace();
                                }
                            }
                        };
                        ping.run();
//                        executorService.execute(ping);
                    }

//                    while(count != devicesFound.size()){
//
//                    }
                }

            });


            return deviceDataList;
        }

        @Override
        protected void onPostExecute(List<DeviceData> deviceData) {
            Log.d("HOME","ENCONTRADOS->"+deviceData.toString());
        }

    }

}
