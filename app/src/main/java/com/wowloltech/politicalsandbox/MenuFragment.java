package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

public class MenuFragment extends Fragment implements View.OnClickListener {
    String LOG_TAG = "myLog";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fragment, null);
        v.findViewById(R.id.button_resume).setOnClickListener(this);
        v.findViewById(R.id.button_exit).setOnClickListener(this);
        v.findViewById(R.id.menu_btn_save).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_resume:
                getActivity().onBackPressed();
                break;
            case R.id.button_exit:
                getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("save_database", Tools.dbHelper.getDatabaseName()).apply();
                Tools.dbHelper.getDb().close();
                getActivity().finish();
                break;
            case R.id.menu_btn_save:
                String saveName = Tools.dbHelper.getDb().getPath();
                File file1 = new File(saveName);
                File file2 = new File(saveName.substring(0, saveName.length()-3) + " copy.db");
                try {
                    Tools.copy(file1, file2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        }

    }
}
