package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.os.AsyncTask;
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
import com.mesh.syncband.data.Setlist;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.dao.SetlistDao;
import com.mesh.syncband.fragments.dialog.NewSetlistDialog;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SetlistsFragment extends Fragment {

    private ArrayAdapter<String> adapter;
    private List<String> setlists = null;
    private SetlistDao setlistDao;

    public SetlistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SETLISTS DEFAULT PARA VISUALIZAÇÃO
        this.setlists = Arrays.asList(getResources().getStringArray(R.array.setlists_example));
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

        getActivity().setTitle("Setlists");
        this.adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);

        ListView listView = view.findViewById(R.id.setlistsView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = setlists.get(i);
                Bundle args = new Bundle();
                args.putString("currentSetlist",selected);

                //redirect to specify setlist
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
                FragmentManager fragmentManager = getFragmentManager();
                NewSetlistDialog newSetlistDialog = new NewSetlistDialog();
                newSetlistDialog.show(fragmentManager,"nova_setlist_dialog");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        setlistDao = AppDatabase.getAppDatabase(getActivity()).setlistDao();
        AsyncTask<Void, Void, List<String>> asyncTask = new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                return setlistDao.getAllNames();
            }
        };
        List<String> strings = null;
        try {
            strings = asyncTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        adapter.addAll(strings);
        if (strings.isEmpty())
            getView().findViewById(R.id.setlistsMessage).setVisibility(View.VISIBLE);
        else
            getView().findViewById(R.id.setlistsMessage).setVisibility(View.INVISIBLE);

    }
}
