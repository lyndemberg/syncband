package com.mesh.syncband.grpc;

import android.content.Context;
import android.util.Log;

import com.mesh.syncband.PropertiesUtil;
import com.mesh.syncband.database.ProfileDao;
import com.mesh.syncband.model.Profile;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.model.Song;
import com.stealthcopter.networktools.IPTools;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.Server;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

public class MetronomeServer extends MetronomeServiceGrpc.MetronomeServiceImplBase {

    private static final String TAG = ".MetronomeServer";

    private Map<DeviceData,StreamObserver<Data>> observers = new HashMap<>();

    private Setlist setlist;
    private String password;

    private ProfileDao profileDao;

    private Server server;
    private boolean running = false;
    private Context context;
    private SongStart songRunning;
    private SongStart currentSong;
    private List<Song> songList;
    private int currentPosition = 0;

    public MetronomeServer(Context context, ProfileDao profileDao){
        this.context = context;
        this.profileDao = profileDao;
        this.currentSong = SongStart.newBuilder().setArtist("")
                .setName("").setBpm(0).build();
    }

    public void start(Setlist set, List<Song> songs, String pass) throws IOException {
        this.setlist = set;
        this.password = pass;
        this.songList = songs;

        server = NettyServerBuilder.forPort(PropertiesUtil.PORT_SERVER_GRPC)
                .addService(this)
                .build();

        server.start();
        running = true;
        Song first = songList.get(currentPosition);
        currentSong = SongStart.newBuilder().setArtist(first.getArtist())
                                        .setName(first.getName())
                                        .setBpm(first.getBpm()).build();

        Log.i(TAG, "Server started, listening on " + PropertiesUtil.PORT_SERVER_GRPC);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                Log.e(TAG, "*** shutting down gRPC server since JVM is shutting down");
                MetronomeServer.this.stop();
                Log.e(TAG, "*** server shutdown");
            }
        });
    }

    public void stop() {
        if (server != null) {
            Data flowDisconnect = Data.newBuilder().setType(Data.Type.DISCONNECT).build();
            for(Map.Entry<DeviceData, StreamObserver<Data>> observerEntry: observers.entrySet()){
                StreamObserver<Data> observer = observerEntry.getValue();
                try{
                    observer.onNext(flowDisconnect);
                    observer.onCompleted();
                }catch (StatusRuntimeException e){
                    continue;
                }
            }
            server.shutdown();
            running = false;
            observers = new HashMap<>();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void updateCurrentSong(int position){
        this.currentPosition = position;
        Song song = songList.get(position);
        currentSong = SongStart.newBuilder().setArtist(song.getArtist())
                            .setName(song.getName())
                            .setBpm(song.getBpm()).build();
    }

    public void notifySong() {
        Data songStart = Data.newBuilder().setType(Data.Type.SONG_START)
                .setSong(this.currentSong).build();
        for(Map.Entry<DeviceData, StreamObserver<Data>> observerEntry: observers.entrySet()){
            StreamObserver<Data> observer = observerEntry.getValue();
            try{
                observer.onNext(songStart);
            }catch (StatusRuntimeException e){
                continue;
            }
        }
    }

    public void notifyPause(){
        Data songStart = Data.newBuilder().setType(Data.Type.SONG_PAUSE).build();
        for(Map.Entry<DeviceData, StreamObserver<Data>> observerEntry: observers.entrySet()){
            StreamObserver<Data> observer = observerEntry.getValue();
            try{
                observer.onNext(songStart);
            }catch (StatusRuntimeException e){
                continue;
            }
        }
    }

//    public void notifyPlayPause(){
//        Data data = Data.newBuilder().setType(Data.Type.PLAY_PAUSE_SONG).build();
//        for(Map.Entry<DeviceData, StreamObserver<Data>> observerEntry: observers.entrySet()){
//            StreamObserver<Data> observer = observerEntry.getValue();
//            observer.onNext(data);
//        }
//    }

    public SongStart getCurrentSong() {
        return currentSong;
    }

    public List<Song> getSongList() {
        return songList;
    }

    @Override
    public void ping(Void request, StreamObserver<DeviceData> responseObserver) {
        Profile profile = profileDao.getProfileSync();
        DeviceData deviceData = DeviceData.newBuilder().setHost(IPTools.getLocalIPv4Address().getHostAddress())
                .setNickname(profile.getNickName())
                .setFunction(profile.getFunction())
                .build();

        responseObserver.onNext(deviceData);
        responseObserver.onCompleted();
    }

    @Override
    public void connect(Credentials request, StreamObserver<Data> responseObserver) {
        if(!request.getPassword().equals(password)){
            Data flowFail = Data.newBuilder().setType(Data.Type.AUTHORIZATION_FAIL).build();
            responseObserver.onNext(flowFail);
            responseObserver.onCompleted();
        }else{
            Data flowSuccess = Data.newBuilder().setType(Data.Type.AUTHORIZATION_SUCCESS).build();
            responseObserver.onNext(flowSuccess);

            observers.put(request.getDevice(), responseObserver);

//            Data currentSong = Data.newBuilder().setType(Data.Type.SONG_START).setSong(this.currentSong).build();
//            responseObserver.onNext(currentSong);
        }
    }

    @Override
    public void disconnect(DeviceData request, StreamObserver<Void> responseObserver) {
        StreamObserver<Data> dataStreamObserver = observers.get(request);
        dataStreamObserver.onNext(Data.newBuilder().setType(Data.Type.DISCONNECT).build());
        dataStreamObserver.onCompleted();

        responseObserver.onNext(Void.newBuilder().build());
        responseObserver.onCompleted();
        observers.remove(request);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public SongStart getSongRunning() {
        return songRunning;
    }

    public void setSongRunning(SongStart songRunning) {
        this.songRunning = songRunning;
        notifySong();
    }
}
