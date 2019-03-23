package com.mesh.syncband.socket;


import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.socket.valueobject.DeviceData;
import com.mesh.syncband.socket.valueobject.SongStart;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetronomeServerSingleton {

    private DeviceData deviceData;
    private static final int PORT = 44444;
    private ServerSocket server;
    private static MetronomeServerSingleton instance = null;
    private ExecutorService executor;
    private List<Socket> clients;
    private String password;
    private boolean status;
    private Setlist setlist;

    public void setSetlist(Setlist setlist) {
        this.setlist = setlist;
    }

    private MetronomeServerSingleton(){
        executor = Executors.newFixedThreadPool(2);
        clients = new ArrayList<>();
    }

    public static MetronomeServerSingleton getInstance(){
        if(instance == null){
            instance = new MetronomeServerSingleton();
        }

        return instance;
    }

    public void setDeviceData(DeviceData deviceData){
        this.deviceData = deviceData;
    }

    public void setPassword(String pass) throws NotSetPasswordException{
        if(password != null){
            throw new NotSetPasswordException();
        }
        password = pass;
    }

    public String getPassword(){
        return this.password;
    }


    public void start() throws NotPossibleBindServer{
        if (!status) {
            try {
                server = new ServerSocket();
                server.bind(new InetSocketAddress("localhost", PORT));
                status = true;
            } catch (IOException e) {
                throw new NotPossibleBindServer();
            }
        }
    }

    public void initializeHandler() {
        while (status) {
            Socket accept = null;
            try {
                accept = server.accept();
                HandlerRequest request = new HandlerRequest(accept, this);
                executor.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws IOException {
        if(status){
            notifyClientsInShutDown();
            status = false;
            clients.clear();
            server.close();
            executor.shutdown();
            instance = null;
        }
    }

    public void notifyClientsInPlay(final SongStart start){
        for(final Socket server : clients){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
                        JSONObject data = new JSONObject();
                        data.put("type","start");
                        data.put("data",start);
                        out.writeObject(start);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            executor.execute(runnable);
        }
    }

    private void notifyClientsInShutDown(){
        for(final Socket server : clients){
            try {
                ObjectOutputStream out = new ObjectOutputStream(server.getOutputStream());
                JSONObject data = new JSONObject();
                data.put("type","shutdown");
                out.writeObject(data);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectClient(Socket add){
        clients.add(add);
    }

    public void disconnectClient(Socket socket) {
        clients.remove(socket);
        socket = null;
    }

    public DeviceData getDeviceData(){
        return this.deviceData;
    }

    public boolean isStatus() {
        return status;
    }
}
