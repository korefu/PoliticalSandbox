package com.wowloltech.politicalsandbox;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class MainFragment extends Fragment implements View.OnClickListener {
    NewGameFragment newGameFragment;
    Button newGameButton;
    Button continueButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, null);
        Tools.mainFragment = this;
        newGameButton = (Button) v.findViewById(R.id.btn_new_game);
        continueButton = (Button) v.findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(this);
        newGameButton.setOnClickListener(this);
        newGameFragment = new NewGameFragment();
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

                    startActivity(new Intent(getActivity().getApplicationContext(), GameActivity.class));
            }
        else
            Toast.makeText(getActivity(), "Игра сохраняется...", Toast.LENGTH_SHORT).show();
    }
}
