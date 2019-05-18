package com.wowloltech.politicalsandbox;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

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
    boolean newGame = true;
    boolean AITurn = false;
    private boolean isMenuHidden = true;
    private Game game;
    AsyncTask gameThread;
    Button button;

    public Game getGame() {
        return game;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPref = getSharedPreferences("save", MODE_PRIVATE);
        setContentView(R.layout.activity_game);
        game = new Game(this);
        game.startGame(sPref.getString("map_database", "testmap.db"), sPref.getString("save_database", "testsave.db"));
        setViews(savedInstanceState);
        button = findViewById(R.id.btn_nextTurn);
        gameView = new GameView(this, game);
        map.addView(gameView);
        button.setEnabled(false);
        if (game.getCurrentPlayer() != null) {
            button.setText(R.string.next_turn);
            newGame = false;
            button.setEnabled(true);
        }
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
        if (p != null) {
            textPlayer.setText(p.toString());
            textMoney.setText("Казна: " + new DecimalFormat("#0.00").format(p.getMoney()) + "(" + new DecimalFormat("#0.00").format(p.getMoneyIncome()) + ")");
            textRecruits.setText("Рекруты: " + p.getRecruits() + "(" + p.getRecruitsIncome() + ")");
            textTurn.setText("Ход: " + game.getTurnCounter());
        }
    }


    @Override
    public void onClick(View view) {
        if (!gameView.isArmyMoving()) {
            if (view.getId() == R.id.btn_nextTurn) {
                if (!newGame) nextTurn();
                else {
                    List<Player> players = new LinkedList<>();
                    for (int i = 0; i < game.getPlayers().size(); i++) {
                        Player player = game.getPlayers().get(i);
                        if (gameView.getSelectedProvince().getOwner() == player)
                            players.add(new HumanPlayer(player.getId(), player.getMoney(), player.getRecruits(), player.getColor(),
                                    player.getName(), player.getProvinces(), player.getArmies()));
                        else
                            players.add(new AIPlayer(player.getId(), player.getMoney(), player.getRecruits(), player.getColor(),
                                    player.getName(), player.getProvinces(), player.getArmies()));
                    }
                    game.getPlayers().clear();
                    game.getPlayers().addAll(players);
                    game.setCurrentPlayer(game.findPlayerByID(sPref.getInt("player_id", 0)));
                    sPref.edit().remove("player_id").apply();
                    button.setText(R.string.next_turn);
                    newGame = false;
                    game.getCurrentPlayer().nextTurn();
                    if (sPref.getString("new_or_load", "load").equals("new"))
                        currentTurn(game.getCurrentPlayer());
                }
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
                gameView.neighbours = a.getLocation().getNeighbours();
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
                game.getCurrentPlayer().combineArmy(gameView.getSelectedProvince());
                updateScreen();
                break;
            case MENU_DIVIDE_ARMY:
                provinceDivideArmyDialog.show(getFragmentManager(), "provinceSelectArmy");
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void nextTurn() {
        gameThread = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                nextTurn.setEnabled(false);
                AITurn = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                HumanPlayer humanPlayer = (HumanPlayer) game.nextTurn();
                game.setCurrentPlayer(humanPlayer);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if (game.getCurrentPlayer() != null) {
                    nextTurn.setEnabled(true);
                    currentTurn(game.getCurrentPlayer());
                    updateScreen();
                    AITurn = false;
                } else {
                    Tools.dbHelper.getDb().close();
                    Log.d("myLog", Tools.dbHelper.getDatabaseName());
                    deleteDatabase(Tools.dbHelper.getDatabaseName());
                    sPref.edit().putString("save_database", "null").commit();
                    GameActivity.this.finish();
                }
            }
        }.
                execute();
    }

    public void setViews(Bundle b) {
        textMoney = findViewById(R.id.text_money);
        textPlayer = findViewById(R.id.text_player);
        textTurn = findViewById(R.id.text_turn);
        textRecruits = findViewById(R.id.text_recruits);
        nextTurn = findViewById(R.id.btn_nextTurn);
        nextTurn.setOnClickListener(this);
        map = findViewById(R.id.map);
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
            isMenuHidden = !isMenuHidden;
        } else {
            fTrans.remove(menuFragment).commit();
            isMenuHidden = !isMenuHidden;
        }


    }


}
