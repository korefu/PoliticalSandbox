package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class LoadGameFragment extends Fragment implements View.OnClickListener {
    private boolean isStarting = true;
    RecyclerView recyclerView;
    DataAdapter adapter;
    List<String> saves;
    String selectedSave = null;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.load_game_fragment, null);
        v.findViewById(R.id.loadgame_btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.loadgame_btn_delete).setOnClickListener(this);
        v.findViewById(R.id.loadgame_btn_load).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerView);
        saves = new ArrayList<>(Arrays.asList(getActivity().getApplicationContext().databaseList()));
        ListIterator<String>
                iterator = saves.listIterator();
        while (iterator.hasNext()) {
            String map = iterator.next();
            if (!map.substring(map.length() - 3).equals(".db")) iterator.remove();
            else iterator.set(map.substring(0, map.length() - 3));
        }
        Log.d("myLog", saves.toString());
        adapter = new DataAdapter(getActivity(), saves);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedSave = saves.get(position);
                pos = position;
            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loadgame_btn_load:
                if (selectedSave != null) {
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", selectedSave+".db").commit();
                    isStarting = false;
                    selectedSave = null;
                    startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
                }
                break;
            case R.id.loadgame_btn_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.loadgame_btn_delete:
                if (selectedSave != null) {
                    getActivity().deleteDatabase(selectedSave+".db");
                    selectedSave = null;
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", "null").commit();
                    saves.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
                break;
        }
    }
    public void onResume() {
        super.onResume();
        if (!isStarting)
            getActivity().onBackPressed();
        isStarting = true;
    }
}
