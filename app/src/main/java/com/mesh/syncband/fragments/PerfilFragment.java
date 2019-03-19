package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mesh.syncband.R;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.model.Profile;

public class PerfilFragment extends Fragment {

    private TextInputEditText inputNickname;
    private TextInputEditText inputFunction;
    private ProfileRepository profileRepository;
    private Profile profile;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileRepository = new ProfileRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        inputNickname = view.findViewById(R.id.input_nickname);
        inputFunction = view.findViewById(R.id.input_function);
        Button button = view.findViewById(R.id.button_update_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profile !=null){
                    profileRepository.updateProfile(profile);
                }else{
                    profile = new Profile();
                    profile.setNickName(inputNickname.getText().toString());
                    profile.setFunction(inputFunction.getText().toString());
                    profileRepository.insertProfile(profile);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Perfil");
    }

    @Override
    public void onStart() {
        super.onStart();
        profileRepository.getProfile().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile prof) {
                if(prof != null){
                    inputNickname.setText(prof.getNickName());
                    inputFunction.setText(prof.getFunction());
                    profile = prof;
                }
            }
        });
    }
}
