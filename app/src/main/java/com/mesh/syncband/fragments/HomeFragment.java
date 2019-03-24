package com.mesh.syncband.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesh.syncband.MainApplication;
import com.mesh.syncband.R;
import com.mesh.syncband.fragments.dialog.ListServersDialog;
import com.mesh.syncband.grpc.MetronomeServer;
import com.mesh.syncband.grpc.service.DeviceData;
import com.mesh.syncband.grpc.service.MetronomeServiceGrpc;
import com.mesh.syncband.grpc.service.Void;
import com.stealthcopter.networktools.SubnetDevices;
import com.stealthcopter.networktools.subnet.Device;

import java.util.ArrayList;

import javax.inject.Inject;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class HomeFragment extends Fragment {


    @Inject
    MetronomeServer metronomeServer;

    private TextView status;
    private TextView currentBpm;
    private TextView currentSong;
    private TextView messageDisconnect;
    private Button buttonDisconnect;
    private LinearLayout layoutVolume;
    private Button buttonSearch;

    public HomeFragment() {
    }


    @Override
    public void onAttach(Context context) {
        ((MainApplication) context.getApplicationContext()).getComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonSearch = view.findViewById(R.id.button_search);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager childFragmentManager = getChildFragmentManager();
                ListServersDialog listServersDialog = new ListServersDialog();
                listServersDialog.show(childFragmentManager,ListServersDialog.class.getSimpleName());
//                new SearchServersTask().execute();
            }
        });

        status = view.findViewById(R.id.status);
        currentBpm = view.findViewById(R.id.current_bpm);
        currentSong = view.findViewById(R.id.current_song);
        buttonDisconnect = view.findViewById(R.id.button_disconnect);
        layoutVolume = view.findViewById(R.id.layout_volume);
        messageDisconnect = view.findViewById(R.id.message_disconnect);

        return view;
    }

    private void showInServer(){
        messageDisconnect.setVisibility(View.INVISIBLE);
        buttonSearch.setVisibility(View.INVISIBLE);
        status.setVisibility(View.VISIBLE);
        currentBpm.setVisibility(View.VISIBLE);
        currentSong.setVisibility(View.VISIBLE);
        layoutVolume.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("SyncBand - Home");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(metronomeServer.isRunning()){
            showInServer();
        }
    }


}
