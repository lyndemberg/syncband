package com.mesh.syncband.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mesh.syncband.R;
import com.mesh.syncband.adapters.SongResultAdapter;
import com.mesh.syncband.services.SearchService;
import com.mesh.syncband.valueobject.SongResult;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    public static final int SEARCH_REQUEST = 1;
    public static final int SONG_SELECTED = 2;

    public static final String SONG_SELECTED_TO_ADD = "song_selected";

    private static final String TAG = ".activities.SearchActivity";

    private RecyclerView recyclerView;
    private SongResultAdapter adapter;
    private ResultReceiver searchHandler;
    private TextView searchMessage;
    private TextView textViewInfo;

    private ProgressBar progressBar;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchHandler = new SearchResultReceiver(new Handler());
        progressBar = findViewById(R.id.progress_bar);
        textViewInfo = findViewById(R.id.textViewInfo);

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
                progressBar.setVisibility(ProgressBar.VISIBLE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()){
                    adapter.setDataset(new ArrayList<SongResult>());
                    adapter.notifyDataSetChanged();
                }
                return true;
            }

        });


        searchMessage = findViewById(R.id.search_message);

        recyclerView = findViewById(R.id.list_results);
        adapter = new SongResultAdapter(this, new ArrayList<SongResult>(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                SongResult item = adapter.getItem(itemPosition);
                Intent resultSearch = new Intent();
                resultSearch.putExtra(SONG_SELECTED_TO_ADD,item);
                setResult(SONG_SELECTED, resultSearch);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
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
                List<SongResult> results = (List<SongResult>) resultData.getSerializable("results");
                adapter.setDataset(results);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if (results.isEmpty()){
                    searchMessage.setText("Nada encontrado!");
                    searchMessage.setVisibility(View.VISIBLE);
                    textViewInfo.setVisibility(View.INVISIBLE);
                }else{
                    searchMessage.setVisibility(View.INVISIBLE);
                    textViewInfo.setVisibility(View.VISIBLE);
                }
            }else if(resultCode == RESULT_CODE_FAIL){
                searchMessage.setText("Nao foi possivel realizar a busca!");
                searchMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
