package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.content.Context;
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

import com.mesh.syncband.MainApplication;
import com.mesh.syncband.R;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.database.SongRepository;
import com.mesh.syncband.grpc.MetronomeServer;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;
import com.mesh.syncband.valueobject.ServerTaskProgress;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;


public class ServerFragment extends Fragment {

    @Inject
    SetlistRepository setlistRepository;
    @Inject
    SongRepository songRepository;
    @Inject
    MetronomeServer metronomeServer;
    @Inject
    ProfileRepository profileRepository;

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
        View view = inflater.inflate(R.layout.fragment_server, container, false);
        spinnerSetlists = view.findViewById(R.id.spinner_setlists);
        inputPassword = view.findViewById(R.id.input_password);
        buttonIniciar = view.findViewById(R.id.buttonIniciar);
        buttonStop = view.findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metronomeServer.stop();
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
        if(metronomeServer.isRunning()){
            updateViewInRunning();
        }else{
            updateViewWhenNotRunning();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setlistRepository.getAllNames().removeObserver(observerListSetlists);
    }

    private void updateViewInRunning(){
        buttonIniciar.setVisibility(View.INVISIBLE);
        buttonStop.setVisibility(View.VISIBLE);
        spinnerSetlists.setEnabled(false);
        inputPassword.setEnabled(false);
    }


    private void updateViewWhenNotRunning(){
        buttonIniciar.setVisibility(View.VISIBLE);
        buttonStop.setVisibility(View.INVISIBLE);
        spinnerSetlists.setEnabled(true);
        inputPassword.setEnabled(true);
    }

    public class ServerTask extends AsyncTask<String,ServerTaskProgress,Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String password = strings[1];
            Setlist setlist = setlistRepository.findByNameSync(strings[0]);
            List<Song> songs = songRepository.findAllBySetlistSync(setlist.getId());
            Profile profile = profileRepository.getProfileSync();

            if(profile==null){
                publishProgress(ServerTaskProgress.PROFILE_NULL);
            }else if(songs == null){
                publishProgress(ServerTaskProgress.SETLIST_SONGS_EMPTY);
            }else{
                try {
                    metronomeServer.start(setlist, songs, password);
                    metronomeServer.blockUntilShutdown();
                } catch (IOException e) {
                    cancel(true);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    cancel(true);
                    e.printStackTrace();
                }
            }

            return null;
        }


        @Override
        protected void onProgressUpdate(ServerTaskProgress... values) {
            ServerTaskProgress progress = values[0];
            switch (progress){
                case PROFILE_NULL:
                    Toast.makeText(getContext(), "Perfil não definido!",Toast.LENGTH_LONG).show();
                    break;
                case SETLIST_SONGS_EMPTY:
                    Toast.makeText(getContext(), "Nenhuma música na setlist!",Toast.LENGTH_LONG).show();
                    break;
                case RUNNING:
                    updateViewInRunning();
                    Toast.makeText(getContext(), "Servidor iniciado!",Toast.LENGTH_LONG).show();
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateViewWhenNotRunning();
            Toast.makeText(getContext(),"Servidor desligado",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onCancelled() {
            updateViewWhenNotRunning();
            Toast.makeText(getContext(),"Não foi possível iniciar o server!",Toast.LENGTH_LONG).show();
        }
    }


}
