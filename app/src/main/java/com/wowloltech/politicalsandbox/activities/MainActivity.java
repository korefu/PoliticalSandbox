package com.wowloltech.politicalsandbox.activities;

import android.app.Activity;
import android.os.Bundle;

import com.wowloltech.politicalsandbox.R;

public class MainActivity extends Activity {
    public static boolean rewrite = false;

    public MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainFragment = new MainFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.main_frame, mainFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}

