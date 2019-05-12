package com.wowloltech.politicalsandbox;

import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Player {
    private int id;
    private List<Province> provinces;
    private List<Player> wars;
    private List<Event> events;
    private List<Army> armies;
    private Ruler ruler;
    private double money;
    private int recruits;
    private boolean isHuman;
//    public int recruits;

    public Player(int id) {
        this.id = id;
        provinces = new ArrayList<>();
        wars = new LinkedList<>();
        events = new LinkedList<>();
        armies = new ArrayList<>();
        this.ruler = new Ruler(3, 3, 3);
        this.money = 0;
        this.recruits = 1000;
    }

    public Player(int id, double money, int recruits) {
        this.id = id;
        this.money = money;
        this.recruits = recruits;
        armies = new ArrayList<>();
        provinces = new ArrayList<>();
        wars = new LinkedList<>();
        events = new LinkedList<>();
        this.ruler = new Ruler(3, 3, 3);
    }


    public int getId() {
        return id;
    }

    public void addArmy(Army a) {
        armies.add(a);
        ContentValues cv = new ContentValues();
        cv.put("strength", a.getStrength());
        cv.put("location", a.getLocation().getId());
        cv.put("speed", a.getSpeed());
        cv.put("owner", a.getOwner().getId());
        cv.put("id", a.getId());
        Tools.dbHelper.getDb().insert("armies", null, cv);
    }


    public List<Province> getProvinces() {
        return provinces;
    }

    public List<Player> getWars() {
        return wars;
    }

    public void setWars(List<Player> wars) {
        this.wars = wars;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Army> getArmies() {
        return armies;
    }

    public Ruler getRuler() {
        return ruler;
    }

    public void setRuler(Ruler ruler) {
        this.ruler = ruler;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
        ContentValues cv = new ContentValues();
        cv.put("money", money);
        Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId())});
    }

    public int getRecruits() {
        return recruits;
    }

    public void setRecruits(int recruits) {
        this.recruits = recruits;
        ContentValues cv = new ContentValues();
        cv.put("recruits", recruits);
        Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId())});
    }

    public void setIsHuman(boolean isHuman) {
        this.isHuman = isHuman;
        ContentValues cv = new ContentValues();
        if (isHuman) {
            cv.put("is_human", 1);
            Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId())});
        } else {
            cv.put("is_human", 0);
            Tools.dbHelper.getDb().update("players", cv, "_id = ?", new String[]{String.valueOf(getId())});
        }
    }

    public boolean isHuman() {
        return isHuman;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                '}';
    }

    public double getMoneyIncome() {
        double income = 0;
        for (int i = 0; i < this.provinces.size(); i++)
            income += provinces.get(i).getIncome();
        for (int i = 0; i < this.armies.size(); i++)
            income -= (double) armies.get(i).getStrength() / 100;
        return income;
    }

    public int getRecruitsIncome() {
        int income = 0;
        for (int i = 0; i < this.provinces.size(); i++)
            income += provinces.get(i).getRecruits();
        return income;
    }

    private void unleashWar(Player p) {
        wars.add(p);
        p.wars.add(this);
        p.events.add(Event.WAR);
    }

    public void divideArmy(Game game, int strength1, int strength2, Army parentArmy) {
        game.getCurrentPlayer().addArmy(new Army(strength1, parentArmy.getLocation(), game.getCurrentPlayer(), game.getIdCounter(), parentArmy.getSpeed()));
        game.getCurrentPlayer().addArmy(new Army(strength2, parentArmy.getLocation(), game.getCurrentPlayer(), game.getIdCounter() + 1, parentArmy.getSpeed()));
        game.setIdCounter(game.getIdCounter() + 2);
        Army.remove(parentArmy, game);
    }

    abstract boolean acceptAlliance(Event e);

    abstract boolean acceptPeace(Event e);

    public void pickMilitary(Game game, int strength, Province province) {
        game.getCurrentPlayer().addArmy(new Army(strength, province, game.getCurrentPlayer(), game.getIdCounter()));
        game.setIdCounter(game.getIdCounter() + 1);
        game.getCurrentPlayer().setRecruits(getRecruits() - strength);
        game.getCurrentPlayer().setMoney(getMoney() - (double) strength / 50);
    }

    public void combineArmy(Game game, Province selectedProvince) {
        int summaryArmy = 0;
        int summarySpeed = 10;
        for (Iterator<Army> i = game.getCurrentPlayer().getArmies().iterator(); i.hasNext(); ) {
            Army cArmy = i.next();
            if (cArmy.getLocation() == selectedProvince) {
                summaryArmy += cArmy.getStrength();
                if (summarySpeed > cArmy.getSpeed())
                    summarySpeed = cArmy.getSpeed();
                Army.remove(cArmy, i);
            }
        }
        Log.d("myLog", "" + summaryArmy);
        if (summaryArmy != 0) {
            game.getCurrentPlayer().addArmy(new Army(summaryArmy, selectedProvince, game.getCurrentPlayer(), game.getIdCounter(), summarySpeed));
            game.setIdCounter(game.getIdCounter() + 1);
        }

    }

    public abstract void nextTurn();
}
