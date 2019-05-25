package com.wowloltech.politicalsandbox;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import com.wowloltech.politicalsandbox.activities.EditorActivity;
import com.wowloltech.politicalsandbox.activities.EditorView;
import com.wowloltech.politicalsandbox.activities.GameActivity;
import com.wowloltech.politicalsandbox.activities.GameView;
import com.wowloltech.politicalsandbox.activities.MainActivity;
import com.wowloltech.politicalsandbox.models.Army;
import com.wowloltech.politicalsandbox.models.Map;
import com.wowloltech.politicalsandbox.models.Player;
import com.wowloltech.politicalsandbox.models.Province;

import java.util.LinkedList;
import java.util.List;

public class Game {
    private final String LOG_TAG = "myLog";
    private Player currentPlayer;
    private GameActivity activity;
    private EditorActivity editorActivity;
    private int turnCounter = 0;
    private List<Player> players;
    private DatabaseHelper dbHelper;
    private GameView gameView;
    private EditorView editorView;
    private int idCounter = 0;

    public Game(GameActivity activity) {
        this.activity = activity;
        this.players = new LinkedList<>();
    }

    public Game(EditorActivity activity) {
        editorActivity = activity;
        this.players = new LinkedList<>();
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public GameActivity getActivity() {
        return activity;
    }

    Player addPlayerFromDb(int id, double money, int recruits, int color, String name) {
        return new Player(id, money, recruits, color, name, this);
    }

    void setTurnCounterFromDb(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    private void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
        ContentValues cv = new ContentValues();
        cv.put("turn_counter", turnCounter);
        dbHelper.getDb().update("game", cv, null, null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player nextTurn() {
        int i = 0;
        for (int k = 0; k < players.size(); k++)
            if (players.get(k).getId() == currentPlayer.getId())
                i = k + 1;
        while (true) {
            i %= players.size();
            if (i == 0) {
                Map.nextTurn();
                for (Player p : this.players)
                    for (Army a : p.getArmies())
                        a.setSpeed(1);
                setTurnCounter(turnCounter + 1);
            }

            for (; i < players.size(); i++) {
                Log.d("myLog", getPlayers().get(i).toString());
                if (currentPlayer != null)
                    if (players.size() == 1) {
                        return null;
                    }
                if (players.get(i).getProvinces().size() == 0) {
                    removePlayer(players.get(i).getId());
                    i--;
                    continue;
                }
                if (players.get(i).isHuman()) {
                    players.get(i).nextTurn();
                    return players.get(i);
                }
                players.get(i).nextTurn();
            }
        }
    }

    public void editMap(String saveName) {
        dbHelper = new DatabaseHelper(editorActivity, "raw_map", saveName);
        dbHelper.readDatabase(this);
    }


    public void startGame(String dbName, String saveName) {
        Log.d("myLog", "startGame");
        if (MainActivity.rewrite) {
            try {
                activity.deleteDatabase(saveName);
                Log.d("myLog", "deleted");
            } catch (Exception e) {
                Log.d("myLog", "not fund");
            }
            dbHelper = new DatabaseHelper(activity.getApplicationContext(), dbName, saveName);
        } else
            dbHelper = new DatabaseHelper(activity.getApplicationContext(), dbName, saveName);
        Log.d("myLog", dbName);
        dbHelper.readDatabase(this);
    }

    public Player findPlayerByID(int id) {
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getId() == id)
                return players.get(i);
        return null;
    }

    private void removePlayer(int playerId) {
        dbHelper.getDb().delete("players", "_id = ?", new String[]{String.valueOf(playerId + 1)});
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getId() == playerId) {
                players.remove(i);
                break;
            }
    }

    public Player addPlayer(int id, double money, int recruits, int color, String name) {
        ContentValues cv = new ContentValues();
        cv.put("money", money);
        cv.put("recruits", recruits);
        cv.put("color", color);
        cv.put("name", name);
        Log.d("myLog",  dbHelper.getDatabaseName());
        dbHelper.getDb().insert("players", null, cv);
        Player player = new Player(id, money, recruits, color, name, this);
        players.add(player);
        return player;
    }

    public void removeArmy(Army army) {
        army.getOwner().getArmies().remove(army);
        army.getLocation().getArmies().remove(army);
        dbHelper.getDb().delete("armies", "_id = ?", new String[]{String.valueOf(army.getId())});
    }

    public void attackProvince(Army army, Province province) {
        if (province.getOwner() != army.getOwner()) {
            for (; province.getArmies().size() > 0; ) {
                Army a = province.getArmies().get(0);
                if (province.getOwner() != army.getOwner()) {
                    if (a.getStrength() > army.getStrength()) {
                        a.setStrength(a.getStrength() - army.getStrength());
                        removeArmy(army);
                        return;
                    } else if (a.getStrength() == army.getStrength()) {
                        removeArmy(army);
                        removeArmy(a);
                        a.getOwner().getArmies().remove(a);
                        return;
                    } else {
                        army.setStrength(army.getStrength() - a.getStrength());
                        removeArmy(a);
                        a.getOwner().getArmies().remove(a);
                    }
                }
            }
            province.getOwner().getProvinces().remove(province);
            army.getOwner().getProvinces().add(province);
            province.setOwner(army.getOwner());
            gameView.invalidate();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void updateArmyDb(int id, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        dbHelper.getDb().update("armies", cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public void updatePlayerDb(int id, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(id + 1)});
    }

    public void updateMapDb(int id, String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        dbHelper.getDb().update("map", cv, "_id = ?", new String[]{String.valueOf(id + 1)});
    }

    public void addArmyToDb(Army a) {
        ContentValues cv = new ContentValues();
        cv.put("strength", a.getStrength());
        cv.put("location", a.getLocation().getId());
        cv.put("speed", a.getSpeed());
        cv.put("owner", a.getOwner().getId());
        cv.put("_id", a.getId());
        getDb().insert("armies", null, cv);
    }


    public void updateScreen() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            Runnable rn = new Runnable() {
                @Override
                public void run() {
                    activity.updateScreen();
                    synchronized (this) {
                        this.notify();
                    }
                }
            };
            synchronized (rn) {
                activity.runOnUiThread(rn);
                try {
                    rn.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SQLiteDatabase getDb() {
        return dbHelper.getDb();
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idC) {
        idCounter = idC;
        ContentValues cv = new ContentValues();
        cv.put("id_counter", idCounter);
        dbHelper.getDb().update("game", cv, null, null);
    }

    public void setEditorView(EditorView editorView) {
        this.editorView = editorView;
    }
}
