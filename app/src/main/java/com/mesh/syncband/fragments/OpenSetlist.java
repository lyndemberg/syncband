package com.mesh.syncband.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mesh.syncband.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenSetlist extends Fragment {

    String currentSetlist = "";

    public OpenSetlist() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_abrir_setlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Editar setlist");
        this.currentSetlist = getArguments().getString("currentSetlist");
        TextView textView = view.findViewById(R.id.setlistAberta);
        textView.setText(currentSetlist);
    }
}
