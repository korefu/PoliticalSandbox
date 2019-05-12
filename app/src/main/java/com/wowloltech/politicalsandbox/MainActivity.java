package com.wowloltech.politicalsandbox;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

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
