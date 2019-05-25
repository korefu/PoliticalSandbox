package com.wowloltech.politicalsandbox.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.wowloltech.politicalsandbox.R;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class MainFragment extends Fragment implements View.OnClickListener {
    NewGameFragment newGameFragment;
    LoadGameFragment loadGameFragment;
    Button newGameButton;
    Button continueButton;
    Button loadGameButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.main_fragment, null);
        newGameButton = v.findViewById(R.id.btn_new_game);
        continueButton = v.findViewById(R.id.btn_continue);
        loadGameButton = v.findViewById(R.id.btn_load_game);
        continueButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
        loadGameButton.setOnClickListener(this);
        newGameFragment = new NewGameFragment();
        loadGameFragment = new LoadGameFragment();
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_new_game:
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_frame, newGameFragment)
                        .commit();
                break;
            case R.id.btn_continue:
                if (Objects.equals(getActivity().getSharedPreferences("save", MODE_PRIVATE).getString("save_database", "null"), "null"))
                    Toast.makeText(getActivity(), "Последней сохраненной игры не существует", Toast.LENGTH_SHORT).show();
                else {
                    getActivity().getSharedPreferences("save", Activity.MODE_PRIVATE).edit().putString("new_or_load", "load").apply();
                    MainActivity.rewrite = false;
                    startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
                }
                break;
            case R.id.btn_load_game:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main_frame, loadGameFragment)
                            .commit();
                }
                break;
        }
    }
}
