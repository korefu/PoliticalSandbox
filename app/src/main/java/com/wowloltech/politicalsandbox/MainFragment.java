package com.wowloltech.politicalsandbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class MainFragment extends Fragment implements View.OnClickListener {
    NewGameFragment newGameFragment;
    LoadGameFragment loadGameFragment;
    Button newGameButton;
    Button continueButton;
    Button loadGameButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, null);
        Tools.mainFragment = this;
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
        if (Tools.isSaved)
            switch (view.getId()) {
                case R.id.btn_new_game:
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main_frame, newGameFragment)
                            .commit();
                    break;
                case R.id.btn_continue:
                    if (getActivity().getSharedPreferences("save", MODE_PRIVATE).getString("save_database", "null").equals("null"))
                        Toast.makeText(getActivity(), "Последней сохраненной игры не существует", Toast.LENGTH_SHORT).show();
                    else {
                        MainActivity.rewrite = false;
                        startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
                    }
                    break;
                case R.id.btn_load_game:
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main_frame, loadGameFragment)
                            .commit();
            }
        else
            Toast.makeText(getActivity(), "Игра сохраняется...", Toast.LENGTH_SHORT).show();
    }
}
