package com.mesh.syncband.fragments.dialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import com.mesh.syncband.model.Setlist;

import java.util.Date;

public class NewSetlistDialog extends DialogFragment {

    private EditText inputSetlistName;

    public interface NewSetlistListener{
        void toSave(Setlist setlist);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputSetlistName = new EditText(getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nova setlist");
        builder.setMessage("Digite o nome da setlist");
        builder.setView(inputSetlistName);
        builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            Date createdInstant = new Date();
            Setlist toSaveValue = new Setlist(inputSetlistName.getText().toString(),createdInstant,createdInstant);
            NewSetlistListener listener = (NewSetlistListener) getParentFragment();
            listener.toSave(toSaveValue);
            dismiss();
            }
        });

        return builder.create();
    }
}
