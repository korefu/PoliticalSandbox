package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;


public class NewGameFragment extends Fragment implements View.OnClickListener {
    EditText editText;
    EditText playerId;
    RecyclerView recyclerView;
    Switch ha;
    ArrayList<String> maps;
    String saveName;
    DataAdapter adapter;
    private boolean isStarting = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_fragment, null);
        editText = v.findViewById(R.id.selected_database);
        ha = v.findViewById(R.id.new_game_ha);
        ha.setChecked(true);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        recyclerView = v.findViewById(R.id.newgame_recyclerView);
        try {
            maps = new ArrayList<>(Arrays.asList(getActivity().getAssets().list("")));
            Log.d("myLog", maps.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ListIterator<String> iterator = maps.listIterator();
        while (iterator.hasNext()) {
            String map = iterator.next();
            if (map.length() <= 6) {
                iterator.remove();
                continue;
            }
            if (!map.substring(map.length() - 3).equals(".db")) {
                iterator.remove();
            } else iterator.set(map.substring(0, map.length() - 3));
        }

        adapter = new DataAdapter(getActivity(), maps);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                boolean collision = false;
                final String[] saves = getActivity().getApplicationContext().databaseList();
                if (editText.getText().length()==0)
                    saveName = editText.getHint().toString() + ".db";
                else saveName = editText.getText().toString() + ".db";
                for (String s : saves)
                    if (s.equals(saveName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Вы точно хотите перезаписать текущее сохранение?")
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startNewGame(position);
                                    }
                                })
                                .setNegativeButton(R.string.btn_cancel,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        collision = true;
                        break;
                    }
                if (!collision)
                    startNewGame(position);
            }
        });
        return v;
    }

    public void startNewGame(int position) {
        String selectedSave = maps.get(position);
        MainActivity.rewrite = true;
        getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", saveName).commit();
        getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("map_database", selectedSave + ".db").commit();
        getActivity().getSharedPreferences("settings", Activity.MODE_PRIVATE).edit().putBoolean("ha", ha.isChecked()).commit();
        getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("new_or_load", "new").apply();
        startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
        isStarting = false;
    }
    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }

    public void onResume() {
        super.onResume();
        if (!isStarting)
            getActivity().onBackPressed();
        isStarting = true;
    }
}
