package com.mesh.syncband.grpc;

import android.content.Context;

import com.mesh.syncband.PropertiesUtil;
import com.mesh.syncband.model.Song;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

public class MetronomeClient {

    private static final String TAG = ".MetronomeClient";

    private ManagedChannel channel;
    private Song currentSong;
    private Context context;
    private boolean isConnected;
    private DeviceData serverData;
    private DeviceData clientData;
    private MetronomeServiceGrpc.MetronomeServiceBlockingStub stub;

    public MetronomeClient(Context context){
        this.context = context;
    }

    public Iterator<Data> connect(DeviceData serverData, Credentials credentials){
        channel = NettyChannelBuilder.forAddress(serverData.getHost(), PropertiesUtil.PORT_SERVER_GRPC).usePlaintext().build();
        stub = MetronomeServiceGrpc.newBlockingStub(channel);
        Iterator<Data> connect = stub.connect(credentials);
        return connect;
    }


    public void disconnect() throws InterruptedException {
        if(isConnected){
            MetronomeServiceGrpc.MetronomeServiceBlockingStub stub = MetronomeServiceGrpc.newBlockingStub(channel);
            Void disconnect = stub.disconnect(clientData);
            this.isConnected = false;
            channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        }
    }


    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

    public DeviceData getServerData() {
        return serverData;
    }

    public void setServerData(DeviceData serverData) {
        this.serverData = serverData;
    }

    public DeviceData getClientData() {
        return clientData;
    }

    public void setClientData(DeviceData clientData) {
        this.clientData = clientData;
    }
}
