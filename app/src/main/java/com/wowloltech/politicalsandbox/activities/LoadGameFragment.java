package com.wowloltech.politicalsandbox.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wowloltech.politicalsandbox.R;

import java.io.File;
import java.io.IOException;
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
        View v = View.inflate(getActivity(), R.layout.load_game_fragment, null);
        v.findViewById(R.id.loadgame_btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.loadgame_btn_delete).setOnClickListener(this);
        v.findViewById(R.id.loadgame_btn_load).setOnClickListener(this);
        v.findViewById(R.id.btn_editor).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.recyclerView);
        saves = new ArrayList<>(Arrays.asList(getActivity().getApplicationContext().databaseList()));
        File[] externalSaves = new File(Environment.getExternalStorageDirectory().toString() + "/Political Sandbox saves").listFiles();
        for (File externalSave : externalSaves) saves.add(externalSave.getName());
        ListIterator<String>
                iterator = saves.listIterator();
        while (iterator.hasNext()) {
            String map = iterator.next();
            if (map.length() <= 3) {
                iterator.remove();
                continue;
            }
            if (!map.substring(map.length() - 3).equals(".db")) {
                iterator.remove();
            } else iterator.set(map.substring(0, map.length() - 3));
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

    @SuppressLint("ApplySharedPref")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loadgame_btn_load:
                MainActivity.rewrite = false;
                if (selectedSave != null) {
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", selectedSave + ".db").commit();
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("new_or_load", "load").apply();
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
                    getActivity().deleteDatabase(selectedSave + ".db");
                    File f = new File(Environment.getExternalStorageDirectory().toString() + "/Political Sandbox saves/" + selectedSave + ".db");
//                    Log.d("myLog", f.getPath());
                    if (f.exists()) {
                        try {
                            f.getCanonicalFile().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    selectedSave = null;
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", "null").commit();
                    saves.remove(pos);
                    adapter.notifyItemRemoved(pos);
                }
                break;
            case R.id.btn_editor:
                if (selectedSave != null)
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", selectedSave + ".db").commit();
                else {
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", "Temp edit save.db").commit();
                }
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("new_or_load", "load").apply();
                isStarting = false;
                selectedSave = null;
                startActivity(new Intent(getActivity().getApplicationContext(), EditorActivity.class));
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
