package com.mesh.syncband.socket;

import com.mesh.syncband.socket.valueobject.Credentials;
import com.mesh.syncband.socket.valueobject.DeviceData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HandlerRequest implements Runnable{

    private Socket socket;
    private MetronomeServerSingleton server;

    public HandlerRequest(Socket accept, MetronomeServerSingleton metronomeServerSingleton){
        this.socket = accept;
        this.server = metronomeServerSingleton;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            JSONObject request = (JSONObject) objectInputStream.readObject();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            switch (request.getString("action")){
                case "PING":
                    DeviceData deviceData = server.getDeviceData();
                    out.writeObject(deviceData);
                    out.flush();
                    socket.close();
                    break;
                case "CONNECT":
                    //send data in stream
                    Credentials credentials = (Credentials) request.get("credentials");
                    if(!credentials.password.equals(server.getPassword())){
                        //reject connect
                        out.writeBoolean(false);
                    }else{
                        //accept connect
                        server.connectClient(socket);
                        out.writeBoolean(true);
                    }
                    out.flush();
                    break;
                case "DISCONNECT":
                    server.disconnectClient(socket);
                    out.writeBoolean(true);
                    out.flush();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
