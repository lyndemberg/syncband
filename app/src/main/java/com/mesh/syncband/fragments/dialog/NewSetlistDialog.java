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

import com.mesh.syncband.R;
import com.mesh.syncband.model.Setlist;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.DaoAccess;

import java.util.Date;

public class NewSetlistDialog extends DialogFragment {

    private EditText inputSetlistName;

    public interface SetListCreatedListener{
        void setlistCreated();
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
        builder.setIcon(R.drawable.ic_add_circle_black_24dp);
        builder.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Date createdInstant = new Date();
                Setlist toSave = new Setlist(inputSetlistName.getText().toString(),createdInstant,createdInstant);
                new SaveNewSetlist().execute(toSave);
            }
        });

        return builder.create();
    }


    private class SaveNewSetlist extends AsyncTask<Setlist,Void,Void>{

        @Override
        protected Void doInBackground(Setlist... setlists) {
            DaoAccess daoAccess = AppDatabase.getAppDatabase(getActivity()).daoAccess();
            daoAccess.save(setlists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SetListCreatedListener listener = (SetListCreatedListener) getParentFragment();
            listener.setlistCreated();
            dismiss();
        }
    }
}
