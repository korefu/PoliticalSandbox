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
    private int turnCounter = 0;
    private List<Player> players;
    private DatabaseHelper myDbHelper;

    public Game(GameActivity activity) {
        this.activity = activity;
        this.players = new LinkedList<>();
        Tools.game = this;
    }

    public GameActivity getActivity() {
        return activity;
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
        Tools.setIdCounter(idCounter);
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.updateScreen();
                    }
                });
            }
        }
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
            myDbHelper = new DatabaseHelper(activity.getApplicationContext(), dbName, saveName);
        } else
            myDbHelper = new DatabaseHelper(activity.getApplicationContext(), dbName, saveName);
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

    public void removePlayer(int playerId) {
        Tools.dbHelper.getDb().delete("players", "_id = ?", new String[]{String.valueOf(playerId+1)});
        for (int i = 0; i < players.size(); i++)
            if (players.get(i).getId() == playerId) {
                players.remove(i);
                break;
            }
    }

}
