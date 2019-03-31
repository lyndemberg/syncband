package com.mesh.syncband.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mesh.syncband.MainApplication;
import com.mesh.syncband.R;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.fragments.dialog.AuthenticationDialog;
import com.mesh.syncband.fragments.dialog.ListServersDialog;
import com.mesh.syncband.grpc.Credentials;
import com.mesh.syncband.grpc.DeviceData;
import com.mesh.syncband.grpc.Flow;
import com.mesh.syncband.grpc.MetronomeClient;
import com.mesh.syncband.grpc.MetronomeServer;
import com.mesh.syncband.grpc.SongStart;
import com.mesh.syncband.model.Profile;
import com.stealthcopter.networktools.IPTools;

import java.util.Iterator;

import javax.inject.Inject;


public class HomeFragment extends Fragment implements ListServersDialog.ListServersListener {


    @Inject
    MetronomeServer metronomeServer;
    @Inject
    MetronomeClient metronomeClient;
    @Inject
    ProfileRepository profileRepository;

    private TextView status;
    private TextView currentBpm;
    private TextView currentSong;
    private TextView messageDisconnect;
    private Button buttonDisconnect;
    private LinearLayout layoutVolume;
    private Button buttonSearch;
    private ImageButton previousButton;
    private ImageButton nextButton;

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

        previousButton = view.findViewById(R.id.button_previous);
        nextButton = view.findViewById(R.id.button_next);

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previous = metronomeServer.getCurrentPosition() - 1;
                metronomeServer.updateCurrentSong(previous);
                updateCurrentSongInView(metronomeServer.getCurrentSong());
                updateButtonsPreviousAndNext();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int next = metronomeServer.getCurrentPosition() + 1;
                metronomeServer.updateCurrentSong(next);
                updateCurrentSongInView(metronomeServer.getCurrentSong());
                updateButtonsPreviousAndNext();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager childFragmentManager = getChildFragmentManager();
                ListServersDialog listServersDialog = new ListServersDialog();
                listServersDialog.show(childFragmentManager,ListServersDialog.class.getSimpleName());
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

    private void updateButtonsPreviousAndNext(){
        int size = metronomeServer.getSongList().size();
        int currentPosition = metronomeServer.getCurrentPosition();
        if((currentPosition + 1) < size){
            nextButton.setEnabled(true);
        }else{
            nextButton.setEnabled(false);
        }
        if((currentPosition-1)!=-1){
            previousButton.setEnabled(true);
        }else{
            previousButton.setEnabled(false);
        }
    }

    private void showInServer(){
        //invisible
        messageDisconnect.setVisibility(View.INVISIBLE);
        buttonSearch.setVisibility(View.INVISIBLE);
        //visible
        status.setVisibility(View.VISIBLE);
        status.setText("Server ligado");
        currentBpm.setVisibility(View.VISIBLE);
        currentSong.setVisibility(View.VISIBLE);
        layoutVolume.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        currentSong.setVisibility(View.VISIBLE);
        currentBpm.setVisibility(View.VISIBLE);
        updateButtonsPreviousAndNext();
        updateCurrentSongInView(metronomeServer.getCurrentSong());
    }

    private void showInClient(){
        //invisible
        buttonSearch.setVisibility(View.INVISIBLE);
        messageDisconnect.setVisibility(View.INVISIBLE);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        //visible
        status.setVisibility(View.VISIBLE);
        buttonDisconnect.setVisibility(View.VISIBLE);
        layoutVolume.setVisibility(View.VISIBLE);
        currentSong.setVisibility(View.VISIBLE);
        currentBpm.setVisibility(View.VISIBLE);
    }

    private void showInSearch(){
        //invisible
        status.setVisibility(View.INVISIBLE);
        buttonDisconnect.setVisibility(View.INVISIBLE);
        layoutVolume.setVisibility(View.INVISIBLE);
        currentBpm.setVisibility(View.INVISIBLE);
        currentSong.setVisibility(View.INVISIBLE);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        //visible
        messageDisconnect.setVisibility(View.VISIBLE);
        buttonSearch.setVisibility(View.VISIBLE);
    }

    private void updateCurrentSongInView(SongStart songStart){
        currentSong.setText(songStart.getName()+" - "+songStart.getArtist());
        currentBpm.setText(songStart.getBpm()+"");
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
        }else if(metronomeClient.isConnected()){
            showInClient();
        }else{
            showInSearch();
        }
    }


    @Override
    public void notifyServerSelected(final DeviceData deviceData) {
        final EditText inputPassword = new EditText(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Insira a senha");
        builder.setMessage("Server: " + deviceData.getNickname());
        builder.setView(inputPassword);
        builder.setCancelable(false);
        builder.setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClientTask connectTask = new ClientTask(deviceData);
                connectTask.execute(inputPassword.getText().toString());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });


        builder.create().show();
    }

    public class ClientTask extends AsyncTask<String, Flow, Void> {

        private DeviceData serverData;

        public ClientTask(DeviceData serverData){
            this.serverData = serverData;
        }

        @Override
        protected Void doInBackground(String... strings) {
            String password = strings[0];

            Profile profileSync = profileRepository.getProfileSync();
            if(profileSync == null) {
                cancel(true);
            }else{

                DeviceData clientData = DeviceData.newBuilder()
                        .setHost(IPTools.getLocalIPv4Address().getHostAddress())
                        .setNickname(profileSync.getNickName())
                        .setFunction(profileSync.getFunction())
                        .build();


                Credentials credentials = Credentials.newBuilder().setDevice(clientData).setPassword(password).build();

                Iterator<Flow> connect = metronomeClient.connect(serverData, credentials);
                while(connect.hasNext()){
                    Flow next = connect.next();
                    publishProgress(next);
                }

            }

            return null;
        }


        @Override
        protected void onProgressUpdate(Flow... values) {
            Flow flow  = values[0];
            switch (flow.getTypeFlow()){
                case AUTHORIZATION_SUCCESS:
                    Toast.makeText(getContext(),"Você está conectado a " + serverData.getNickname(),Toast.LENGTH_LONG).show();
                    status.setText("Conectado a " + serverData.getNickname());
                    metronomeClient.setConnected(true);
                    showInClient();
                    break;
                case AUTHORIZATION_FAIL:
                    Toast.makeText(getContext(),"Senha incorreta!",Toast.LENGTH_LONG).show();
                    showInSearch();
                    break;
                case DISCONNECT:
                    Toast.makeText(getContext(), "Server desligado",Toast.LENGTH_LONG).show();
                    showInSearch();
                    break;
                case SONG_START:
                    SongStart start = flow.getSong();
                    updateCurrentSongInView(start);
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getContext(),"Defina o seu perfil primeiro!",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }
}
