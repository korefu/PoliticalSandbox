package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;


public class NewGameFragment extends Fragment implements View.OnClickListener, OnCheckedChangeListener {
    EditText editText;
    private boolean isStarting = true;

    @Override
    public void onCheckedChanged(CompoundButton p1, boolean p2) {
        if (p2) {
            getActivity().getSharedPreferences("editor", Activity.MODE_PRIVATE).edit().putBoolean("editor", true).apply();
        } else {
            getActivity().getSharedPreferences("editor", Activity.MODE_PRIVATE).edit().putBoolean("editor", false).apply();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_game_fragment, null);
        v.findViewById(R.id.btn_start).setOnClickListener(this);
        ToggleButton tb = (ToggleButton) v.findViewById(R.id.enabled_editor_button);
        tb.setOnCheckedChangeListener(this);
        editText = (EditText) v.findViewById(R.id.selected_database);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("selected_database", editText.getText().toString()).apply();
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
