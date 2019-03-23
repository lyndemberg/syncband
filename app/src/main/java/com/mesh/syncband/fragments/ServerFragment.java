package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.mesh.syncband.R;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.socket.MetronomeServerSingleton;
import com.mesh.syncband.socket.NotPossibleBindServer;
import com.mesh.syncband.socket.NotSetPasswordException;
import com.mesh.syncband.socket.valueobject.DeviceData;
import com.stealthcopter.networktools.IPTools;

import java.io.IOException;
import java.util.List;


public class ServerFragment extends Fragment {

    private MetronomeServerSingleton server;
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
                new ServerStopTask().execute();
            }
        });

        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedString = spinnerSetlists.getSelectedItem().toString();
                ServerStartTask serverAsync = new ServerStartTask();
                serverAsync.execute(selectedString);
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

    private class ServerStartTask extends AsyncTask<String,Boolean,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            server = MetronomeServerSingleton.getInstance();

            Setlist setlist = setlistRepository.findByNameSync(strings[0]);
            Profile profile = profileRepository.getProfileSync();
            DeviceData deviceData = new DeviceData();
            deviceData.host = IPTools.getLocalIPv4Address().getHostAddress();
            deviceData.nameProfile = profile.getNickName();
            deviceData.function = profile.getFunction();

            server.setSetlist(setlist);
            server.setDeviceData(deviceData);

            try{
                server.setPassword(inputPassword.getText().toString());
                server.start();
                Log.d("SERVER","INICIOU");
                publishProgress(true);
                server.initializeHandler();
            }catch(NotPossibleBindServer e){
                publishProgress(false);
                cancel(true);
            }catch(NotSetPasswordException e){
                publishProgress(false);
                cancel(true);
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Boolean... values) {
            if(values[0]){
                //started success
                buttonIniciar.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class ServerStopTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
