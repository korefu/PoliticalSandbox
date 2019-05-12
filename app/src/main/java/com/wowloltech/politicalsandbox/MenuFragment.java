package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuFragment extends Fragment implements View.OnClickListener {
    String LOG_TAG = "myLog";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fragment, null);
        v.findViewById(R.id.button_resume).setOnClickListener(this);
        v.findViewById(R.id.button_save).setOnClickListener(this);
        v.findViewById(R.id.button_load).setOnClickListener(this);
        v.findViewById(R.id.button_exit).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_resume:
                getActivity().onBackPressed();
                break;
            case R.id.button_exit:
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("selected_database", Tools.dbHelper.getDatabaseName()).apply();
                getActivity().finish();
        }

    }
}
