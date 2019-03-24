package com.mesh.syncband.grpc;

import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.grpc.service.Confirmation;
import com.mesh.syncband.grpc.service.Credentials;
import com.mesh.syncband.grpc.service.DeviceData;
import com.mesh.syncband.grpc.service.MetronomeServiceGrpc;
import com.mesh.syncband.grpc.service.SongStart;
import com.mesh.syncband.grpc.service.Void;
import com.mesh.syncband.model.Setlist;

import java.util.ArrayList;
import java.util.List;

import io.grpc.stub.StreamObserver;

public class MetronomeServiceImpl extends MetronomeServiceGrpc.MetronomeServiceImplBase {

    private DeviceData deviceData;
    private Setlist setlist;
    private String password;
    private List<StreamObserver<SongStart>> observers = new ArrayList<>();

    public MetronomeServiceImpl(DeviceData deviceData, Setlist setlist, String password) {
        this.deviceData = deviceData;
        this.setlist = setlist;
        this.password = password;
    }

    @Override
    public void ping(Void request, StreamObserver<DeviceData> responseObserver) {
        responseObserver.onNext(deviceData);
        responseObserver.onCompleted();
    }

    @Override
    public void connect(Credentials request, StreamObserver<Confirmation> responseObserver) {
        if(!request.getPassword().equals(password)){
            Confirmation confirmation = Confirmation.newBuilder().setStatus(false).build();
            responseObserver.onNext(confirmation);
        }else{
            Confirmation confirmation = Confirmation.newBuilder().setStatus(true).build();
            responseObserver.onNext(confirmation);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void initStream(Void request, StreamObserver<SongStart> responseObserver) {

    }

}
