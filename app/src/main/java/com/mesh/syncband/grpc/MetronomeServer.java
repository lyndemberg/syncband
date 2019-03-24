package com.mesh.syncband.grpc;

import android.util.Log;

import com.mesh.syncband.PropertiesUtil;
import com.mesh.syncband.grpc.service.DeviceData;
import com.mesh.syncband.model.Setlist;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

public class MetronomeServer {

    private static final String TAG = ".MetronomeServer";
    private int port = 44444;
    private Server server;
    private boolean status = false;
    private MetronomeServiceImpl metronomeService;

    public MetronomeServer(){
    }

    public void start(DeviceData device, Setlist set, String pass) throws IOException {
        metronomeService = new MetronomeServiceImpl(device,set,pass);

        server = NettyServerBuilder.forPort(PropertiesUtil.PORT_SERVER_GRPC)
                .addService(metronomeService)
                .build();

        server.start();
        status = true;
        Log.i(TAG, "Server started, listening on " + port);
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
            server.shutdown();
            status = false;
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public boolean isRunning() {
        return status;
    }
}
