package com.mesh.syncband.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.mesh.syncband.R;
import com.mesh.syncband.valueobject.SongResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.mesh.syncband.activities.SearchActivity.SearchResultReceiver.RESULT_CODE_FAIL;
import static com.mesh.syncband.activities.SearchActivity.SearchResultReceiver.RESULT_CODE_OK;


public class SearchService extends IntentService {


    private String SCHEME;
    private String HOST;
    private String PATH;

    private OkHttpClient client;


    public SearchService() {
        super("SearchService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.client = new OkHttpClient();
        this.SCHEME = getString(R.string.scheme_api);
        this.HOST = getString(R.string.host_api);
        this.PATH = getString(R.string.path_api);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        ResultReceiver resultReceiver = intent.getParcelableExtra("resultReceiver");

        String query = intent.getStringExtra("query");

        HttpUrl url = new HttpUrl.Builder()
                .scheme(SCHEME)
                .host(HOST)
                .addPathSegments(PATH)
                .addQueryParameter("q", query)
                .build();
        Log.d("AAA", String.valueOf(url));
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


        try (Response response = client.newCall(request).execute()) {
            String data = response.body().string();
            Log.d("AAA",data);
            JSONObject jsonObject = new JSONObject(data);
            List<SongResult> listSongs = new ArrayList<>();
            String next = null;
            if(jsonObject.length() != 0){
                next = jsonObject.getString("next");
                JSONArray songs = jsonObject.getJSONArray("songs");
                listSongs = new ArrayList<>();
                for(int i=0; i<songs.length(); i++){
                    JSONObject song = songs.getJSONObject(i);
                    String name = song.getString("name");
                    String artist = song.getString("artist");
                    double bpm = song.getDouble("bpm");
                    String album = song.getString("album");
                    String photo = song.getString("photo");

                    listSongs.add(new SongResult(name,artist,bpm,album,photo));
                }
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("results", (Serializable) listSongs);
            bundle.putString("next",next);

            resultReceiver.send(RESULT_CODE_OK,bundle);

        } catch (IOException | JSONException e) {
            resultReceiver.send(RESULT_CODE_FAIL,null);
            e.printStackTrace();
        }

    }


}
