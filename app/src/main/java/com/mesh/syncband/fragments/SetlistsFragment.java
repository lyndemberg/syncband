package com.mesh.syncband.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mesh.syncband.R;

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
    }
}
