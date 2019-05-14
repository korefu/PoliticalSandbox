package com.wowloltech.politicalsandbox;


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

public class ProvinceDivideArmyDialog extends DialogFragment implements OnClickListener, SeekBar.OnSeekBarChangeListener {

    final String LOG_TAG = "myLog";
    TextView divideLeft;
    TextView divideRight;
    SeekBar seekBar;
    GameActivity activity;
    Army army;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.menu_divide, null);
        v.findViewById(R.id.btn_ok).setOnClickListener(this);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);

        divideLeft = (TextView) v.findViewById(R.id.menu_divide_text_left);
        divideLeft.setText(String.valueOf(army.getStrength() / 2));
        divideRight = (TextView) v.findViewById(R.id.menu_divide_text_right);
        divideRight.setText(String.valueOf(army.getStrength() / 2));

        seekBar = (SeekBar) v.findViewById(R.id.menu_recruit_seekbar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(50);
        return v;
    }

    public void setArmy(Army army) {
        this.army = army;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "ProvinceDivide: " + ((Button) v).getText());
        switch (v.getId()) {
            case R.id.btn_ok:
                if (seekBar.getProgress() * activity.getGame().getCurrentPlayer().getRecruits() / 100 > 0) {
                    activity.getGame().getCurrentPlayer().divideArmy(Integer.valueOf(divideLeft.getText().toString()), Integer.valueOf(divideRight.getText().toString()), army);
                }
                break;
            case R.id.btn_cancel:
                dismiss();
        }
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "ProvinceDivide: onDismiss");
        activity.updateScreen();
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "ProvinceDivide: onCancel");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        divideLeft.setText(String.valueOf(army.getStrength() * seekBar.getProgress() / 100));
        divideRight.setText(String.valueOf(army.getStrength() * (100 - seekBar.getProgress()) / 100));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }
}
