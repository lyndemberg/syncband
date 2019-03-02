package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mesh.syncband.R;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.DaoAccess;
import com.mesh.syncband.fragments.dialog.NewSetlistDialog;

import java.util.List;

public class SetlistsFragment extends Fragment implements NewSetlistDialog.SetListCreatedListener{

    private ArrayAdapter<String> adapter;
    private List<String> setlists = null;
    private DaoAccess daoAccess;

    public SetlistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoAccess = AppDatabase.getAppDatabase(getContext()).daoAccess();
        adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setlists, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView = view.findViewById(R.id.setlistsView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = setlists.get(i);
                Bundle args = new Bundle();
                args.putString("currentSetlist",selected);

                OpenSetlist openSetlist = new OpenSetlist();
                openSetlist.setArguments(args);
                getFragmentManager()
                        .beginTransaction().replace(R.id.fragment_container, openSetlist)
                        .addToBackStack(null)
                        .commit();
            }
        });

        FloatingActionButton floatButton = view.findViewById(R.id.buttonAddSetlist);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager childFragmentManager = getChildFragmentManager();
                NewSetlistDialog newSetlistDialog = new NewSetlistDialog();
                newSetlistDialog.show(childFragmentManager,"dialog_fragment");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        refreshSetlists();
    }

    private void refreshSetlists(){
        daoAccess.getAllNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                adapter.clear();
                setlists = strings;
                adapter.addAll(setlists);
                if (strings.isEmpty())
                    getView().findViewById(R.id.setlistsMessage).setVisibility(View.VISIBLE);
                else
                    getView().findViewById(R.id.setlistsMessage).setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setlistCreated() {
        refreshSetlists();
    }

//    private class LoadSetlistsDatabase extends AsyncTask<Void,Void,List<String>>{
//
//        @Override
//        protected List<String> doInBackground(Void... voids) {
//            return setlistDao.getAllNames();
//        }
//
//        @Override
//        protected void onPostExecute(List<String> strings) {
//            adapter.clear();
//            setlists = strings;
//            adapter.addAll(setlists);
//            if (strings.isEmpty())
//                getView().findViewById(R.id.setlistsMessage).setVisibility(View.VISIBLE);
//            else
//                getView().findViewById(R.id.setlistsMessage).setVisibility(View.INVISIBLE);
//        }
//    }

}
