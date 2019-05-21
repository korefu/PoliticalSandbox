package com.wowloltech.politicalsandbox.dialogs;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wowloltech.politicalsandbox.activities.GameActivity;
import com.wowloltech.politicalsandbox.R;

public class ProvincePickMilitaryDialog extends DialogFragment implements OnClickListener, SeekBar.OnSeekBarChangeListener {

    final String LOG_TAG = "myLog";
    TextView recruitSelected;
    SeekBar seekBar;
    GameActivity activity;
    int selectedRecruits;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.menu_recruit, null);
        v.findViewById(R.id.btn_ok).setOnClickListener(this);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        recruitSelected = v.findViewById(R.id.menu_recruit_text_selected);
        seekBar = v.findViewById(R.id.menu_recruit_seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        recruitSelected.setText("0");
        return v;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "ProvinceRecruitDialog: " + ((Button) v).getText());
        switch (v.getId()) {
            case R.id.btn_ok:
                if (seekBar.getProgress() * activity.getGame().getCurrentPlayer().getRecruits() / 100 > 0) {
                    activity.getGame().getCurrentPlayer().pickMilitary(selectedRecruits, activity.gameView.getSelectedProvince());
                }
                break;
            case R.id.btn_cancel:
                dismiss();
        }
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "ProvinceRecruitDialog: onDismiss");
        activity.updateScreen();
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "ProvinceRecruitDialog: onCancel");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (activity.getGame().getCurrentPlayer().getMoney() * 50 >= activity.getGame().getCurrentPlayer().getRecruits())
            selectedRecruits = seekBar.getProgress() * activity.getGame().getCurrentPlayer().getRecruits() / 100;
        else
            selectedRecruits = (int) (seekBar.getProgress() * activity.getGame().getCurrentPlayer().getMoney() / 2);
        recruitSelected.setText(String.valueOf(selectedRecruits));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }
}
