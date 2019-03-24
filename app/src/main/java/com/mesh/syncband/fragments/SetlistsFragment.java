package com.mesh.syncband.fragments;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.mesh.syncband.MainApplication;
import com.mesh.syncband.R;
import com.mesh.syncband.activities.ManagerSetlistActivity;
import com.mesh.syncband.adapters.SetlistAdapter;
import com.mesh.syncband.database.AppDatabase;
import com.mesh.syncband.database.SetlistRepository;
import com.mesh.syncband.fragments.dialog.NewSetlistDialog;
import com.mesh.syncband.model.Setlist;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SetlistsFragment extends Fragment implements NewSetlistDialog.NewSetlistListener{

    @Inject
    SetlistRepository setlistRepository;
    private MenuItem optionMenu;

    private FloatingActionButton buttonDelete;
    private FloatingActionButton buttonAdd;

    private RecyclerView recyclerView;
    private SetlistAdapter setlistAdapter;

    public SetlistsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        ((MainApplication) context.getApplicationContext()).getComponent().inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setlistAdapter = new SetlistAdapter(getContext(), new ArrayList<String>(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionAdapter = recyclerView.getChildAdapterPosition(view);
                String item = setlistAdapter.getItem(positionAdapter);
                Intent intent = new Intent(getContext(), ManagerSetlistActivity.class);
                intent.putExtra("currentSetlist",item);
                startActivity(intent);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Setlists");
        View inflate = inflater.inflate(R.layout.fragment_setlists, container, false);

        recyclerView = inflate.findViewById(R.id.setlists_list);
        recyclerView.setAdapter(setlistAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        buttonDelete =  inflate.findViewById(R.id.button_delete_setlist);
        buttonAdd = inflate.findViewById(R.id.buttonAddSetlist);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Integer> positionsChecked = setlistAdapter.getPositionsChecked();
                for(Integer position:positionsChecked){
                    String item = setlistAdapter.getItem(position);
                    setlistRepository.deleteSetlistByName(item);
                    setlistAdapter.notifyItemRemoved(position);
                    setlistAdapter.setShowCheckBox(false);
                    hideItensToRemove();
                }
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
            if(setlistAdapter.isShowCheckBoxes()){
                hideItensToRemove();
            }else{
                showItemsToRemove();
            }
            setlistAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private void hideItensToRemove(){
        buttonDelete.setVisibility(View.GONE);
        buttonAdd.setVisibility(View.VISIBLE);
        setlistAdapter.setShowCheckBox(false);
        setlistAdapter.clearCheckedItems();
        optionMenu.setTitle("Excluir");
    }
    private void showItemsToRemove(){
        buttonAdd.setVisibility(View.INVISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
        setlistAdapter.setShowCheckBox(true);
        setlistAdapter.clearCheckedItems();
        optionMenu.setTitle("Cancelar");
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
            setlistAdapter.setDataset(strings);
            setlistAdapter.notifyDataSetChanged();
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
