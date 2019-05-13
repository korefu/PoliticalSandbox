package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;


public class NewGameFragment extends Fragment implements View.OnClickListener {
    EditText editText;
    Spinner spinner;
    ArrayList<String> maps;
    private boolean isStarting = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_fragment, null);
        v.findViewById(R.id.btn_start).setOnClickListener(this);
        editText = v.findViewById(R.id.selected_database);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        spinner = v.findViewById(R.id.spinner);
        try {
            maps = new ArrayList<>(Arrays.asList(getActivity().getAssets().list("")));
            Log.d("myLog", maps.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ListIterator<String>
                iterator = maps.listIterator();
        while (iterator.hasNext()) {
            String map = iterator.next();
            if (!map.substring(map.length() - 6).equals("map.db")) iterator.remove();
            else iterator.set(map.substring(0, map.length() - 6));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, maps);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("map_database", item + "map.db").apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", editText.getText().toString() + ".db").commit();
                startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
                isStarting = false;
                break;
            case R.id.btn_cancel:
                getActivity().onBackPressed();
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
