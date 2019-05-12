package com.wowloltech.politicalsandbox;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private final String LOG_TAG = "myLog";
    private HumanPlayer currentPlayer;
    private GameActivity activity;
    private int idCounter = 0;
    private int turnCounter = 0;
    private List<Player> players;
    private DatabaseHelper myDbHelper;

    public Game(GameActivity activity) {
        this.activity = activity;
        this.players = new LinkedList<>();
    }

    public void setCurrentPlayerFromDb(HumanPlayer get) {
        this.currentPlayer = get;
    }

    public Player addPlayerFromDb(int id, double money, int recruits, int isHuman) {
        if (isHuman == 1)
            return new HumanPlayer(id, money, recruits);
        else
            return new AIPlayer(id, money, recruits);
    }

    public void setTurnCounterFromDb(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public void setIdCounterFromDb(int idCounter) {
        this.idCounter = idCounter;
    }

    public HumanPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(HumanPlayer currentPlayer) {
        this.currentPlayer = currentPlayer;
        ContentValues cv = new ContentValues();
        cv.put("current_player", currentPlayer.getId());
        Tools.dbHelper.getDb().update("game", cv, null, null);
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
        ContentValues cv = new ContentValues();
        cv.put("id_counter", idCounter);
        Tools.dbHelper.getDb().update("game", cv, null, null);
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
        ContentValues cv = new ContentValues();
        cv.put("turn_counter", turnCounter);
        Tools.dbHelper.getDb().update("game", cv, null, null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public HumanPlayer nextTurn() {
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
                if (players.get(i).getProvinces().size() == 0) {
                    removePlayer(players.get(i).getId());
                    i--;
                    continue;
                }
                if (players.get(i).isHuman()) {
                    players.get(i).nextTurn();
                    return (HumanPlayer) players.get(i);
                }
                players.get(i).nextTurn();
            }
        }
    }

    public void startGame(String dbName) {
        Log.d("myLog", "startGame");
        if ("testmap.db".equals(dbName)) {
            try {
                activity.deleteDatabase("testsave.db");
                Log.d("myLog", "deleted");
            } catch (Exception e) {
                Log.d("myLog", "not fund");
            }
            myDbHelper = new DatabaseHelper(activity.getApplicationContext(), "testmap.db", "testsave.db");
            activity.getSharedPreferences("save", Context.MODE_PRIVATE).edit().putString("selected_database", "testsave.db").apply();
        } else
            myDbHelper = new DatabaseHelper(activity.getApplicationContext(),"testmap.db", "testsave.db");
        Log.d("myLog", dbName);
        Tools.dbHelper = myDbHelper;
        myDbHelper.readDatabase(this);
    }

    public Player findPlayerByID(int id) {
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getId() == id)
                return players.get(i);
        return null;
    }

    public Player addPlayer(int id, double money, int recruits, int isHuman) {
        ContentValues cv = new ContentValues();
        cv.put("money", money);
        cv.put("recruits", recruits);
        cv.put("is_human", isHuman);
        cv.put("id", id);
        Tools.dbHelper.getDb().insert("players", null, cv);
        if (isHuman == 1)
            return new HumanPlayer(id, money, recruits);
        else
            return new AIPlayer(id, money, recruits);
    }

    public void removePlayer(int playerId) {
        Tools.dbHelper.getDb().delete("players", "id = ?", new String[]{String.valueOf(playerId)});
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getId() == playerId) {
                players.remove(i);
                break;
            }
    }

    public boolean attackProvince(Army army, Province province) {
        for (Player p : players) {
            Iterator<Army> i = p.getArmies().iterator();
            while (i.hasNext()) {
                Army a = i.next();
                Log.d("myLog", "" + army.getLocation());
                if (a.getLocation() == province && p.getId() != currentPlayer.getId()) {
                    if (a.getStrength() > army.getStrength()) {
                        a.setStrength(a.getStrength() - army.getStrength());
                        Army.remove(army, this);
                        return true;
                    } else if (a.getStrength() == army.getStrength()) {
                        Army.remove(army, this);
                        Army.remove(a, i);
                        return true;
                    } else {
                        army.setStrength(army.getStrength() - a.getStrength());
                        Army.remove(a, i);
                    }
                }
            }
        }
        army.setLocation(province);
        province.getOwner().getProvinces().remove(province);
        province.setOwner(currentPlayer);
        currentPlayer.getProvinces().add(province);
        return true;
    }
}
