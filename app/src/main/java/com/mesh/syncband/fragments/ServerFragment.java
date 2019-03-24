package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.mesh.syncband.MetronomeServiceImpl;
import com.mesh.syncband.PropertiesUtil;
import com.mesh.syncband.R;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.grpc.service.DeviceData;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.stealthcopter.networktools.IPTools;

import java.io.IOException;
import java.util.List;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;


public class ServerFragment extends Fragment {

    private SetlistRepository setlistRepository;
    private ProfileRepository profileRepository;
    private TextInputEditText inputPassword;
    private Spinner spinnerSetlists;
    private Button buttonIniciar;
    private Button buttonStop;
    private Setlist selected;

    private Observer<List<String>> observerListSetlists = new Observer<List<String>>() {
        @Override
        public void onChanged(@Nullable List<String> strings) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, strings);
            spinnerSetlists.setAdapter(adapter);
        }
    };


    public ServerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlistRepository = new SetlistRepository(getContext());
        profileRepository = new ProfileRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server, container, false);
        spinnerSetlists = view.findViewById(R.id.spinner_setlists);
        inputPassword = view.findViewById(R.id.input_password);
        buttonIniciar = view.findViewById(R.id.buttonIniciar);
        buttonStop = view.findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String setlistSelected = spinnerSetlists.getSelectedItem().toString();
                String password = inputPassword.getText().toString();
                ServerTask serverTask = new ServerTask();
                serverTask.execute(setlistSelected, password);
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
        setlistRepository.getAllNames().observe(this, observerListSetlists);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setlistRepository.getAllNames().removeObserver(observerListSetlists);
    }

    public class ServerTask extends AsyncTask<String,Boolean,Void> {

        private Server server;

        @Override
        protected Void doInBackground(String... strings) {

            Profile profile = profileRepository.getProfileSync();
            Setlist setlist = setlistRepository.findByNameSync(strings[0]);
            DeviceData deviceData = DeviceData.newBuilder().setHost(IPTools.getLocalIPv4Address().getHostAddress())
                    .setNickname("")
                    .setFunction("")
                    .build();

            String password = strings[1];

            metronomeService.setData(deviceData,setlist,password);
            server = NettyServerBuilder.forPort(PropertiesUtil.PORT_SERVER_GRPC)
                    .addService(metronomeService)
                    .build();

            try {
                server.start();
                publishProgress(true);
                server.awaitTermination();
            } catch (IOException e) {
                cancel(true);
                e.printStackTrace();
            } catch (InterruptedException e) {
                cancel(true);
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Boolean... values) {
            if(values[0]){
                buttonIniciar.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
                spinnerSetlists.setEnabled(false);
                inputPassword.setEnabled(false);
            }else{
                buttonIniciar.setEnabled(true);
                spinnerSetlists.setEnabled(true);
                inputPassword.setEnabled(true);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            buttonStop.setVisibility(View.INVISIBLE);
            buttonIniciar.setVisibility(View.VISIBLE);
            buttonIniciar.setEnabled(true);
            spinnerSetlists.setEnabled(true);
            inputPassword.setEnabled(true);
            Toast.makeText(getActivity(),"Servidor desligado",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            buttonStop.setVisibility(View.INVISIBLE);
            buttonIniciar.setVisibility(View.VISIBLE);
            buttonIniciar.setEnabled(true);
            spinnerSetlists.setEnabled(true);
            inputPassword.setEnabled(true);
            Toast.makeText(getActivity(),"Servidor desligado",Toast.LENGTH_LONG).show();
        }
    }


}
