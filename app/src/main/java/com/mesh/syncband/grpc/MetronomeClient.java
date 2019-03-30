package com.mesh.syncband.grpc;

import android.content.Context;

import com.mesh.syncband.PropertiesUtil;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

public class MetronomeClient {

    private static final String TAG = ".MetronomeServer";

    private ManagedChannel channel;
    private Context context;
    private boolean isConnected;
    private DeviceData serverData;
    private DeviceData clientData;
    private MetronomeServiceGrpc.MetronomeServiceBlockingStub stub;

    public MetronomeClient(Context context){
        this.context = context;
    }

    public Iterator<Flow> connect(DeviceData serverData, Credentials credentials){
        channel = NettyChannelBuilder.forAddress(serverData.getHost(), PropertiesUtil.PORT_SERVER_GRPC).usePlaintext().build();
        stub = MetronomeServiceGrpc.newBlockingStub(channel);
        Iterator<Flow> connect = stub.connect(credentials);

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
}
