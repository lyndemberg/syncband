package com.mesh.syncband.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mesh.syncband.R;
import com.mesh.syncband.adapters.SongAdapter;
import com.mesh.syncband.adapters.SongVoAdapter;
import com.mesh.syncband.model.Song;
import com.mesh.syncband.services.SearchService;
import com.mesh.syncband.valueobject.SongVo;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = ".activities.SearchActivity";

    private ListView listViewSongs;
    private SongVoAdapter songAdapter;
    private ResultReceiver searchHandler;
    private List<SongVo> songsList = new ArrayList<>();
    private TextView searchMessage;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        songAdapter = new SongVoAdapter(this, songsList);

        listViewSongs = findViewById(R.id.list_results);
        listViewSongs.setAdapter(songAdapter);

        searchHandler = new SearchResultReceiver(new Handler());

        searchMessage = findViewById(R.id.search_message);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        SearchView searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("LongLogTag")
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(SearchActivity.this,SearchService.class);
                intent.putExtra("resultReceiver",searchHandler);
                intent.putExtra("query",query);
                startService(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    public class SearchResultReceiver extends ResultReceiver{
        public static final int RESULT_CODE_OK = 0;
        public static final int RESULT_CODE_FAIL = 1;

        public SearchResultReceiver(Handler handler) {
            super(handler);
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if(resultCode == RESULT_CODE_OK){
                String next = resultData.getString("next");
                List<SongVo> results = (List<SongVo>) resultData.getSerializable("results");
                songAdapter.clear();
                songAdapter.addAll(results);
                if (results.isEmpty()){
                    searchMessage.setText("Nada encontrado!");
                    searchMessage.setVisibility(View.VISIBLE);
                }else{
                    searchMessage.setVisibility(View.INVISIBLE);
                }
            }else if(resultCode == RESULT_CODE_FAIL){
                searchMessage.setText("Nao foi possivel realizar a busca!");
                searchMessage.setVisibility(View.VISIBLE);
            }
        }
    }

}
