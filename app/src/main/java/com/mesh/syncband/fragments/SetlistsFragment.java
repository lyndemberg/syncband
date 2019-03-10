package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.content.Intent;
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
import com.mesh.syncband.activities.ManagerSetlistActivity;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.fragments.dialog.NewSetlistDialog;
import com.mesh.syncband.model.Setlist;

import java.util.List;

public class SetlistsFragment extends Fragment implements NewSetlistDialog.NewSetlistListener{

    private ArrayAdapter<String> adapter;
    private List<String> setlists = null;
    private SetlistRepository setlistRepository;

    public SetlistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlistRepository = new SetlistRepository(getContext());
        adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Setlists");
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
                Intent intent = new Intent(getContext(), ManagerSetlistActivity.class);
                intent.putExtra("currentSetlist",selected);
                startActivity(intent);
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
        setlistRepository.getAllNames().observe(this, new Observer<List<String>>() {
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
    public void toSave(Setlist setlist) {
        setlistRepository.insertSetlist(setlist);
        refreshSetlists();
    }
}
