package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mesh.syncband.R;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.grpc.Credentials;
import com.mesh.syncband.grpc.MetronomeServiceGrpc;
import com.mesh.syncband.grpc.SongPlay;
import com.mesh.syncband.grpc.Status;
import com.mesh.syncband.grpc.Void;
import com.mesh.syncband.model.Setlist;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;


public class ServerFragment extends Fragment {

    private SetlistRepository setlistRepository;
    private Spinner spinnerSetlists;
    private Button buttonIniciar;
    private Setlist selected;

    public ServerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlistRepository = new SetlistRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server, container, false);
        spinnerSetlists = view.findViewById(R.id.spinner_setlists);
        buttonIniciar = view.findViewById(R.id.buttonIniciar);
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = spinnerSetlists.getSelectedItem().toString();
                setlistRepository.findByName(s).observe(ServerFragment.this, new Observer<Setlist>() {
                    @Override
                    public void onChanged(@Nullable Setlist setlist) {
                        selected = setlist;
                        ServerAsync serverAsync = new ServerAsync();
                        serverAsync.execute(selected);
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Server");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setlistRepository.getAllNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, strings);
                spinnerSetlists.setAdapter(adapter);
            }
        });

    }

    private class ServerAsync extends AsyncTask<Setlist,Void,Void>{

        private Server server;

        @Override
        protected Void doInBackground(Setlist... setlists) {
            int port = 44444;
            try {
                server = ServerBuilder.forPort(port)
                        .addService(new MetronomeService())
                        .build()
                        .start();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
    private class MetronomeService extends MetronomeServiceGrpc.MetronomeServiceImplBase{

        @Override
        public void ping(Void request, StreamObserver<Status> responseObserver) {
            Status status = Status.newBuilder().setIsLive(true).build();
            responseObserver.onNext(status);
            responseObserver.onCompleted();
        }

        @Override
        public void connect(Credentials request, StreamObserver<SongPlay> responseObserver) {
            super.connect(request, responseObserver);
        }

    }
}
