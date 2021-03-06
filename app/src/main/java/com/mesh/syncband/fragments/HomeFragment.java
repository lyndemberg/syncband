package com.mesh.syncband.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mesh.syncband.MainApplication;
import com.mesh.syncband.R;
import com.mesh.syncband.activities.interfaces.ActivityHandlerDrawer;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.fragments.dialog.ListServersDialog;
import com.mesh.syncband.grpc.Credentials;
import com.mesh.syncband.grpc.DeviceData;
import com.mesh.syncband.grpc.Data;
import com.mesh.syncband.grpc.MetronomeClient;
import com.mesh.syncband.grpc.MetronomeServer;
import com.mesh.syncband.grpc.SongStart;
import com.mesh.syncband.activities.interfaces.ActivityBindMetronome;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Song;
import com.mesh.syncband.services.IMetronome;
import com.stealthcopter.networktools.IPTools;

import java.util.Iterator;

import javax.inject.Inject;


public class HomeFragment extends Fragment implements ListServersDialog.ListServersListener {

    private static final String TAG = ".HomeFragment";

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
    private Button buttonPlayPause;
    private TextView msgWaiting;
    private AudioManager audioManager = null;
    private SeekBar volumeBar;
    private IMetronome localMetronome;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainApplication) context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonSearch = view.findViewById(R.id.button_search);
        buttonPlayPause = view.findViewById(R.id.button_play_pause);
        volumeBar = view.findViewById(R.id.volume);
        volumeBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        previousButton = view.findViewById(R.id.button_previous);
        nextButton = view.findViewById(R.id.button_next);

        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
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


        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((ActivityBindMetronome) getActivity()).getMetronomeService() != null){
                    if(((ActivityBindMetronome) getActivity()).getMetronomeService().isPlaying()){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                metronomeServer.notifyPause();
                            }
                        }).start();
                        ((ActivityBindMetronome) getActivity()).getMetronomeService().pause();
                        buttonPlayPause.setText("Tocar");
                        showButtonsPreviousAndNext();
                    }else{
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }).start();
                        metronomeServer.notifySong();
                        ((ActivityBindMetronome) getActivity()).getMetronomeService().play(metronomeServer.getCurrentSong().getBpm());
                        buttonPlayPause.setText("Pausar");
                        hideButtonsPreviousAndNext();
                    }
                }
            }
        });

        buttonDisconnect = view.findViewById(R.id.button_disconnect);
        buttonDisconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    metronomeClient.disconnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        status = view.findViewById(R.id.status);
        currentBpm = view.findViewById(R.id.current_bpm);
        currentSong = view.findViewById(R.id.current_song);

        layoutVolume = view.findViewById(R.id.layout_volume);
        messageDisconnect = view.findViewById(R.id.message_disconnect);
        msgWaiting = view.findViewById(R.id.msg);

        return view;
    }



    private void showButtonsPreviousAndNext(){
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void hideButtonsPreviousAndNext(){
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
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
        buttonPlayPause.setVisibility(View.VISIBLE);
        if(localMetronome!=null && localMetronome.isPlaying()){
            buttonPlayPause.setText("Pausar");
        }else{
            buttonPlayPause.setText("Tocar");
        }
        msgWaiting.setVisibility(View.INVISIBLE);
    }

    private void showInClient(){
        //invisible
        buttonSearch.setVisibility(View.INVISIBLE);
        messageDisconnect.setVisibility(View.INVISIBLE);
        previousButton.setVisibility(View.INVISIBLE);
        nextButton.setVisibility(View.INVISIBLE);
        //visible
        status.setVisibility(View.VISIBLE);
        status.setText("Conectado a " + metronomeClient.getServerData().getNickname());
        buttonDisconnect.setVisibility(View.VISIBLE);
        layoutVolume.setVisibility(View.VISIBLE);
        currentSong.setVisibility(View.VISIBLE);
        currentBpm.setVisibility(View.VISIBLE);
        if(metronomeClient.getCurrentSong() != null){
            updateCurrentSongInView(metronomeClient.getCurrentSong());
        }
        if(metronomeClient.getCurrentSong() != null){
            msgWaiting.setVisibility(View.INVISIBLE);
        }else{
            msgWaiting.setVisibility(View.VISIBLE);
        }

        buttonPlayPause.setVisibility(View.INVISIBLE);
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
        buttonPlayPause.setVisibility(View.INVISIBLE);
        msgWaiting.setVisibility(View.INVISIBLE);
    }

    private void updateCurrentSongInView(Song song){
        currentSong.setText(song.getName()+" - "+song.getArtist());
        currentBpm.setText(song.getBpm()+" BPM");
        msgWaiting.setVisibility(View.INVISIBLE);
    }

    private void updateNotSong(){
        currentSong.setText("");
        currentBpm.setText("");
        msgWaiting.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        if(metronomeServer.isRunning()){
            showInServer();
        }else if(metronomeClient.isConnected()){
            showInClient();
        }else{
            showInSearch();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void notifyServerSelected(final DeviceData deviceData) {

        final EditText inputPassword = new EditText(getContext());
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Conectar-se a servidor");
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


    public class ClientTask extends AsyncTask<String, Data, Void> {

        private DeviceData serverData;
        private DeviceData clientData;

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

                this.clientData = clientData;

                Credentials credentials = Credentials.newBuilder().setDevice(clientData).setPassword(password).build();

                Iterator<Data> connect = metronomeClient.connect(serverData, credentials);
                while(connect.hasNext()){
                    Data next = connect.next();
                    publishProgress(next);
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            Data flow  = values[0];
            switch (flow.getType()){
                case AUTHORIZATION_SUCCESS:
                    Toast.makeText(getContext(),"Você está conectado a " + serverData.getNickname(),Toast.LENGTH_LONG).show();
                    metronomeClient.setConnected(true);
                    metronomeClient.setServerData(serverData);
                    metronomeClient.setClientData(clientData);
                    showInClient();
                    ((ActivityHandlerDrawer) getActivity()).disableDrawerClient();
                    break;
                case AUTHORIZATION_FAIL:
                    Toast.makeText(getContext(),"Senha incorreta!",Toast.LENGTH_LONG).show();
                    showInSearch();
                    break;
                case DISCONNECT:
                    Toast.makeText(getContext(), "Você está desconectado!",Toast.LENGTH_LONG).show();
                    metronomeClient.setConnected(false);
                    ((ActivityBindMetronome) getActivity()).getMetronomeService().pause();
                    metronomeClient.setClientData(null);
                    metronomeClient.setServerData(null);
                    metronomeClient.setCurrentSong(null);
                    showInSearch();
                    ((ActivityHandlerDrawer) getActivity()).enableDrawerClient();
                    break;
                case SONG_START:
                    SongStart start = flow.getSong();
                    ((ActivityBindMetronome) getActivity()).getMetronomeService().play(start.getBpm());
                    Song song = new Song(start.getName(), start.getArtist(), start.getBpm());
                    metronomeClient.setCurrentSong(song);
                    updateCurrentSongInView(song);
                    break;
                case SONG_PAUSE:
                    ((ActivityBindMetronome) getActivity()).getMetronomeService().pause();
                    updateNotSong();
                    metronomeClient.setCurrentSong(null);
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
