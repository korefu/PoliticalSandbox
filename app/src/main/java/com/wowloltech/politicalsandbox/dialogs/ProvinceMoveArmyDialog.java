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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.wowloltech.politicalsandbox.R;
import com.wowloltech.politicalsandbox.activities.GameActivity;
import com.wowloltech.politicalsandbox.models.Army;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ProvinceMoveArmyDialog extends DialogFragment implements OnClickListener {
    final String LOG_TAG = "myLog";
    GameActivity activity;
    List<Army> armies;
    ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog().getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        armies = new LinkedList<>();
        for (Army a : activity.getGame().getCurrentPlayer().getArmies())
            if (a.getLocation() == activity.gameView.getSelectedProvince())
                armies.add(a);
        View v = View.inflate(getActivity(), R.layout.menu_select_army, null);
        listView = v.findViewById(R.id.listView);
        String[] s = new String[armies.size()];
        for (int i = 0; i < s.length; i++)
            s[i] = armies.get(i).toString();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1, s);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);
                activity.movingArmy(armies.get(position));
                dismiss();
            }
        });
        return v;
    }

    public void setActivity(GameActivity activity) {
        this.activity = activity;
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "ProvinceMoveArmyDialog: " + ((Button) v).getText());
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "ProvinceMoveArmyDialog: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "ProvinceMoveArmyDialog: onCancel");
    }
}
