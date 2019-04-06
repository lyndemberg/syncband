package com.mesh.syncband.grpc;

import android.content.Context;
import android.widget.Toast;
import com.mesh.syncband.model.Setlist;
import java.util.HashMap;
import java.util.Map;

import io.grpc.stub.StreamObserver;

public class MetronomeServiceImpl extends MetronomeServiceGrpc.MetronomeServiceImplBase {

    private static final String TAG = ".MetronomeServiceImpl";
    private DeviceData deviceData;
    private Setlist setlist;
    private String password;
    private Map<DeviceData,StreamObserver<Data>> observers = new HashMap<>();
    private Context context;

    public MetronomeServiceImpl(DeviceData deviceData, Setlist setlist, String password) {
        this.deviceData = deviceData;
        this.setlist = setlist;
        this.password = password;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void ping(Void request, StreamObserver<DeviceData> responseObserver) {
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
            Toast.makeText(context,"Cliente: "+request.getDevice().getNickname()+" conectado!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void disconnect(DeviceData request, StreamObserver<Void> responseObserver) {
        observers.remove(request);
        responseObserver.onNext(Void.newBuilder().build());
        responseObserver.onCompleted();
    }

    public void sendSongStart(){

    }

    public void sendDisconnectAllObservers(){
        Data flowDisconnect = Data.newBuilder().setType(Data.Type.DISCONNECT).build();
        for(Map.Entry<DeviceData, StreamObserver<Data>> observerEntry: observers.entrySet()){
            StreamObserver<Data> observer = observerEntry.getValue();
            observer.onNext(flowDisconnect);
            observer.onCompleted();
        }
    }

}
