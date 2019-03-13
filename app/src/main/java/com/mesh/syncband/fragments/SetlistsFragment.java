package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    private MenuItem optionMenu;
    private boolean showCheck;
    private FloatingActionButton buttonDelete;
    private FloatingActionButton buttonAdd;
    private ListView listView;

    public SetlistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setlistRepository = new SetlistRepository(getContext());

        adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1);
        showCheck = false;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Setlists");
        View inflate = inflater.inflate(R.layout.fragment_setlists, container, false);

        listView = inflate.findViewById(R.id.setlistsView);
        buttonDelete =  inflate.findViewById(R.id.button_delete_setlist);
        buttonAdd = inflate.findViewById(R.id.buttonAddSetlist);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!showCheck){
                    String selected = setlists.get(i);
                    Intent intent = new Intent(getContext(), ManagerSetlistActivity.class);
                    intent.putExtra("currentSetlist",selected);
                    startActivity(intent);
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                Toast.makeText(getContext(), "ITEMS -> "+checkedItemPositions.toString(), Toast.LENGTH_LONG).show();
//                for(int i=0; i<checkedItemPositions.size(); i++){
//                    Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager childFragmentManager = getChildFragmentManager();
                NewSetlistDialog newSetlistDialog = new NewSetlistDialog();
                newSetlistDialog.show(childFragmentManager,"dialog_fragment");
            }
        });

        return inflate;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        optionMenu = menu.add("Excluir");
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == optionMenu.getItemId()){
            String title = item.getTitle().toString();
            if(title.equals("Excluir")){
                item.setTitle("Cancelar");
                showCheck = true;
                adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_multiple_choice);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                buttonAdd.setVisibility(View.INVISIBLE);
                buttonDelete.setVisibility(View.VISIBLE);
            }else if(title.equals("Cancelar")){
                item.setTitle("Excluir");
                adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);
                buttonDelete.setVisibility(View.GONE);
                buttonAdd.setVisibility(View.VISIBLE);
                showCheck = false;
            }
            listView.setAdapter(adapter);
            refreshSetlists();
        }
        return true;
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
