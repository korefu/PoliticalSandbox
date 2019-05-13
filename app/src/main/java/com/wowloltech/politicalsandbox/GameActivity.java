package com.wowloltech.politicalsandbox;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class GameActivity extends Activity implements View.OnClickListener {

    final int MENU_GET_PROVINCE_INFO = 1;
    final int MENU_RECRUIT = 2;
    final int MENU_MOVE_ARMY = 3;
    final int MENU_COMBINE_ARMY = 4;
    final int MENU_DIVIDE_ARMY = 5;
    public GameView gameView;
    ProvinceInfoDialog provinceInfoDialog;
    ProvincePickMilitaryDialog provincePickMilitaryDialog;
    ProvinceMoveArmyDialog provinceMoveArmyDialog;
    ProvinceSelectArmyDialog provinceDivideArmyDialog;
    MenuFragment menuFragment;
    Button nextTurn;
    TextView textPlayer;
    TextView textMoney;
    TextView textTurn;
    TextView textRecruits;
    RelativeLayout map;
    SharedPreferences sPref;
    private int color;
    private boolean isMenuHidden = true;
    private Game game;

    public Game getGame() {
        return game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            while (!Tools.isSaved)
                Thread.sleep(5);
        } catch (InterruptedException e) {
        }
        sPref = getSharedPreferences("save", MODE_PRIVATE);
        setContentView(R.layout.activity_game);
        game = new Game(this);
        game.startGame(sPref.getString("map_database", "testmap.db"), sPref.getString("save_database", "testsave.db"));
        setViews(savedInstanceState);
        gameView = new GameView(this, game);
        map.addView(gameView);
        currentTurn(game.getCurrentPlayer());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO: Implement this method
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMenuHidden", isMenuHidden);
        if (menuFragment != null)
            outState.putInt("menuId", menuFragment.getId());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO: Implement this method
        isMenuHidden = savedInstanceState.getBoolean("isMenuHidden");
        super.onRestoreInstanceState(savedInstanceState);

    }


    public void currentTurn(Player p) {
        textPlayer.setText("Игрок: " + p);
        textMoney.setText("Казна: " + new DecimalFormat("#0.00").format(p.getMoney()) + "(" + new DecimalFormat("#0.00").format(p.getMoneyIncome()) + ")");
        textRecruits.setText("Рекруты: " + p.getRecruits() + "(" + p.getRecruitsIncome() + ")");
        textTurn.setText("Ход: " + game.getTurnCounter());
    }


    @Override
    public void onClick(View view) {
        if (!gameView.isArmyMoving()) {
            switch (view.getId()) {
                case R.id.btn_nextTurn:
                    nextTurn();
                    break;
            }
        }
    }

    public void updateScreen() {
        currentTurn(game.getCurrentPlayer());
        gameView.invalidate();
    }

    public void movingArmy(Army a) {
        if (!gameView.isArmyMoving()) {
            if (a.getSpeed() > 0) {
                gameView.neighbours = Map.getNeighbours(a.getLocation());
                for (int i = 0; i < gameView.neighbours.size(); i++) {
                    gameView.neighbours.get(i).setSelected(true);
                }
                gameView.setMovingArmy(a);
                gameView.setIsArmyMoving(true);
                nextTurn.setEnabled(false);
                gameView.invalidate();
            } else
                Toast.makeText(getApplicationContext(), "Ходы закончились", Toast.LENGTH_SHORT).show();
        } else {
            gameView.getMovingArmy().setSpeed(gameView.getMovingArmy().getSpeed() - 1);
            for (int i = 0; i < gameView.neighbours.size(); i++) {
                gameView.neighbours.get(i).setSelected(false);
            }
            gameView.setIsArmyMoving(false);
            nextTurn.setEnabled(true);
            gameView.setMovingArmy(null);
        }
        updateScreen();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, MENU_GET_PROVINCE_INFO, 0, "Узнать информацию о провинции");
        if (game.getCurrentPlayer().getId() == gameView.getSelectedProvince().getOwner().getId()) {
            menu.add(0, MENU_RECRUIT, 0, "Нанять войско");
            menu.add(0, MENU_MOVE_ARMY, 0, "Переместить войско");
            menu.add(0, MENU_COMBINE_ARMY, 0, "Объединить войска");
            menu.add(0, MENU_DIVIDE_ARMY, 0, "Разделить войско");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_GET_PROVINCE_INFO:
                provinceInfoDialog.show(getFragmentManager(), "provinceInfoDialog");
                break;
            case MENU_MOVE_ARMY:
                provinceMoveArmyDialog.show(getFragmentManager(), "movearmy");
                break;
            case MENU_RECRUIT:
                provincePickMilitaryDialog.show(getFragmentManager(), "provincePickMilitaryDialog");
                break;
            case MENU_COMBINE_ARMY:
                game.getCurrentPlayer().combineArmy(game, gameView.getSelectedProvince());
                updateScreen();
                break;
            case MENU_DIVIDE_ARMY:
                provinceDivideArmyDialog.show(getFragmentManager(), "provinceSelectArmy");
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void nextTurn() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                nextTurn.setEnabled(false);
            }

            @Override
            protected Void doInBackground(Void... params) {
                game.setCurrentPlayer(game.nextTurn());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                nextTurn.setEnabled(true);
                if (game.getCurrentPlayer() != null) {
                    currentTurn(game.getCurrentPlayer());
                }
                updateScreen();
            }
        }.execute();
    }

    public void setViews(Bundle b) {
        textMoney = (TextView) findViewById(R.id.text_money);
        textPlayer = (TextView) findViewById(R.id.text_player);
        textTurn = (TextView) findViewById(R.id.text_turn);
        textRecruits = (TextView) findViewById(R.id.text_recruits);
        nextTurn = (Button) findViewById(R.id.btn_nextTurn);
        nextTurn.setOnClickListener(this);
        map = (RelativeLayout) findViewById(R.id.map);
        provinceInfoDialog = new ProvinceInfoDialog();
        provinceInfoDialog.setActivity(this);
        provinceDivideArmyDialog = new ProvinceSelectArmyDialog();
        provinceDivideArmyDialog.setActivity(this);
        provincePickMilitaryDialog = new ProvincePickMilitaryDialog();
        provincePickMilitaryDialog.setActivity(this);
        provinceMoveArmyDialog = new ProvinceMoveArmyDialog();
        provinceMoveArmyDialog.setActivity(this);
        try {
            menuFragment = (MenuFragment) getFragmentManager().findFragmentById(b.getInt("menuId"));
        } catch (Exception ignored) {
        }
        if (menuFragment == null)
            menuFragment = new MenuFragment();

        registerForContextMenu(map);
    }


    @Override
    public void onBackPressed() {
        android.app.FragmentTransaction fTrans = getFragmentManager().beginTransaction();
        if (isMenuHidden) {
            fTrans.add(R.id.layout_game, menuFragment).commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                color = getWindow().getStatusBarColor();
                // getWindow().setStatusBarColor(color - 0x222222);
            }
            isMenuHidden = !isMenuHidden;
        } else {
            fTrans.remove(menuFragment).commit();
            //  getWindow().setStatusBarColor(color);
            isMenuHidden = !isMenuHidden;
        }


    }


}
