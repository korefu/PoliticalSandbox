package com.wowloltech.politicalsandbox;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class ProvinceInfoDialog extends DialogFragment {

    final String LOG_TAG = "myLog";
    TextView owner;
    TextView income;
    GameActivity activity;
    TextView recruits;
    TextView armies;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.menu_get_province_info, null);
        owner = (TextView) v.findViewById(R.id.province_info_owner);
        income = (TextView) v.findViewById(R.id.province_info_income);
        recruits = (TextView) v.findViewById(R.id.province_info_recruits);
        armies = (TextView) v.findViewById(R.id.province_info_armies);
        setText();
        return v;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public void setText() {
        owner.setText("Владелец: " + activity.gameView.getSelectedProvince().getOwner());
        income.setText("Доход: " + activity.gameView.getSelectedProvince().getIncome());
        recruits.setText("Прирост рекрутов: " + activity.gameView.getSelectedProvince().getRecruits());
        StringBuilder name = new StringBuilder();
        for (Player p : activity.getGame().getPlayers())
            for (Army a : p.getArmies())
                if (a.getLocation().getId() == activity.gameView.getSelectedProvince().getId())
                    name.append("Владелец: " + p + "\n" + a.toString());
        armies.setText("Армии: \n" + name);
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "ProvinceInfoDialog: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "ProvinceInfoDialog: onCancel");
    }
}
