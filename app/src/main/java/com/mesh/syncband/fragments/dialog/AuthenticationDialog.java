package com.mesh.syncband.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mesh.syncband.MainApplication;
import com.mesh.syncband.database.ProfileRepository;
import com.mesh.syncband.grpc.Credentials;
import com.mesh.syncband.grpc.DeviceData;
import com.mesh.syncband.grpc.MetronomeClient;
import com.mesh.syncband.model.Profile;
import com.stealthcopter.networktools.IPTools;

import java.util.Iterator;

import javax.inject.Inject;

public class AuthenticationDialog extends DialogFragment {

    private DeviceData serverData;
    private AuthenticationListener listener;

    private EditText inputPassword;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            try {
                this.serverData = DeviceData.parseFrom(getArguments().getByteArray("serverData"));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

        }

        listener = (AuthenticationListener) getParentFragment();
        this.inputPassword = new EditText(getContext());
    }

    public interface AuthenticationListener{
        void toConnect(String password, DeviceData serverData);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Insira a senha");
        builder.setMessage("Server: " + serverData.getNickname());
        builder.setView(inputPassword);
        builder.setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return builder.create();
    }



}
