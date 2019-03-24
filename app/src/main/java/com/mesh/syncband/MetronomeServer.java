package com.mesh.syncband;

import android.content.Context;
import android.util.Log;

import com.mesh.syncband.grpc.service.DeviceData;
import com.mesh.syncband.model.Setlist;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

public class MetronomeServer {

    private static final String TAG = ".MetronomeServer";
    private int port = 44444;
    private Server server;

    public MetronomeServer(){
    }

    public void start(DeviceData device, Setlist set, String pass) throws IOException {
        server = NettyServerBuilder.forPort(PropertiesUtil.PORT_SERVER_GRPC)
                .addService(new MetronomeServiceImpl(device,set,pass))
                .build();
        
        Log.i(TAG, "Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                Log.e(TAG, "*** shutting down gRPC server since JVM is shutting down");
                MetronomeServer.this.stop();
                Log.e(TAG, "*** server shut down");
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

}
