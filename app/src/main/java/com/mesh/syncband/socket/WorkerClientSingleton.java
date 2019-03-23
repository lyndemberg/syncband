package com.mesh.syncband.socket;

import com.mesh.syncband.socket.valueobject.Credentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class WorkerClientSingleton {

    private static final int PORT = 44444;
    private Socket client;
    private ObjectInputStream stream;
    private static WorkerClientSingleton instance = null;

    private WorkerClientSingleton() {
    }

    public static WorkerClientSingleton getInstance(String host){
        if(instance == null){
            instance  = new WorkerClientSingleton();
        }
        return instance;
    }

    public boolean connect(String serverIp, Credentials credentials){
        boolean connected = false;
        try {
            client = new Socket(serverIp,PORT);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            JSONObject request = new JSONObject();
            request.put("action","CONNECT");
            request.put("credentials", credentials);
            out.writeObject(request);
            out.flush();

            //return
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            connected = in.readBoolean();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return connected;
    }

    public void observe(ObserverConnectionData observerConnectionData){
        try {
            stream = new ObjectInputStream(client.getInputStream());
            while(true){
                JSONObject data = (JSONObject) stream.readObject();
                observerConnectionData.handlerData(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean disconnect(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            JSONObject request = new JSONObject();
            request.put("action","DISCONNECT");
            //return
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            return in.readBoolean();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }





}
