package com.wowloltech.politicalsandbox.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.wowloltech.politicalsandbox.Game;
import com.wowloltech.politicalsandbox.R;
import com.wowloltech.politicalsandbox.models.Player;

import java.util.Random;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {
    private Game game;
    private EditorView editorView;
    private RelativeLayout map;
    private ColorPicker cp;
    EditText editText;
    Player selectedPlayer;
    EditText saveNameEditText;
    TextView idTextView;
    int selectedId = 0;
    int selectedColor = 0;
    boolean countryPicking = false;

    public Game getGame() {
        return game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        cp = new ColorPicker(EditorActivity.this, 100, 100, 100);
        cp.enableAutoClose();
        findViewById(R.id.btn_id_plus).setOnClickListener(this);
        findViewById(R.id.btn_id_minus).setOnClickListener(this);
        findViewById(R.id.btn_color_picker).setOnClickListener(this);
        findViewById(R.id.btn_save_name).setOnClickListener(this);
        findViewById(R.id.btn_pick_country).setOnClickListener(this);
        findViewById(R.id.btn_copy_save).setOnClickListener(this);
        idTextView = findViewById(R.id.txt_id);
        editText = findViewById(R.id.edit_name_field);
        saveNameEditText = findViewById(R.id.edittext_name_of_save);
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                selectedColor = cp.getColor();
                Player player = game.findPlayerByID(selectedId);
                if (player != null) player.setColor(selectedColor);
                editorView.invalidate();
            }
        });
        map = findViewById(R.id.map);
        game = new Game(this);
        editorView = new EditorView(this);
        map.addView(editorView);
        game.editMap(getSharedPreferences("save", MODE_PRIVATE).getString("save_database", "testsave.db"));
        selectedPlayer = game.getPlayers().get(0);
        editText.setText(selectedPlayer.getName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_id_minus:
                if (selectedId > 0) {
                    selectedPlayer.setName(editText.getText().toString());
                    selectedId--;
                    changePlayer();
                }
                break;
            case R.id.btn_id_plus:
                selectedPlayer.setName(editText.getText().toString());
                selectedId++;
                changePlayer();
                break;
            case R.id.btn_color_picker:
                cp.show();
                break;
            case R.id.btn_save_name:
                selectedPlayer.setName(editText.getText().toString());
                break;
            case R.id.btn_pick_country:
                countryPicking = true;
                break;
            case  R.id.btn_copy_save:
                game.getDbHelper().exportDatabase(game.getDbHelper().getDatabaseName(), saveNameEditText.getText().toString()+".db", true);
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы точно хотите выйти?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void changePlayer() {
        idTextView.setText(String.valueOf(selectedId));
        selectedPlayer = game.findPlayerByID(selectedId);
        if (selectedPlayer == null) {
            editText.setText(R.string.new_player);
            selectedPlayer = game.addPlayer(Integer.valueOf(idTextView.getText().toString()), 0, 0,
                    getRandomColor(), editText.getText().toString());
        } else editText.setText(selectedPlayer.getName());
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }


}
