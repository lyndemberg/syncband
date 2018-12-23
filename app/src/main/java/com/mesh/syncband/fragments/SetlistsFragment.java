package com.mesh.syncband.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mesh.syncband.R;
import com.mesh.syncband.fragments.dialog.NovaSetlistDialog;

import java.util.Arrays;
import java.util.List;

public class SetlistsFragment extends Fragment {


    public SetlistsFragment() {
        // Required empty public constructor
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

        // SETLISTS DEFAULT PARA VISUALIZAÇÃO
        ListView listView = view.findViewById(R.id.listaSetlists);
        List<String> setlists = Arrays.asList(getResources().getStringArray(R.array.setlists_example));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, setlists);
        listView.setAdapter(adapter);
        //

        FloatingActionButton floatButton = view.findViewById(R.id.buttonAddSetlist);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                NovaSetlistDialog novaSetlistDialog = new NovaSetlistDialog();
                novaSetlistDialog.show(fragmentManager,"nova_setlist_dialog");
            }
        });

    }
}
