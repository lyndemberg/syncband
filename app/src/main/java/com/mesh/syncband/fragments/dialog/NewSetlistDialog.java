package com.mesh.syncband.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mesh.syncband.R;

public class NewSetlistDialog extends DialogFragment {

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Nova setlist");
//        builder.setMessage("Digite o nome da setlist");
//        EditText editText = new EditText(getContext());
//        builder.setView(editText);
//        builder.setIcon(R.drawable.ic_add_circle_black_24dp);
//        builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Log.d("nova_setlist_dialog","criou a nova setlist");
//            }
//        });
//
//        return builder.create();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_setlist_dialog,container,false);
    }
}
